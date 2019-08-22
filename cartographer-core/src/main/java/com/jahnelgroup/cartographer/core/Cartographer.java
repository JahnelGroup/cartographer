package com.jahnelgroup.cartographer.core;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigUtils;
import com.jahnelgroup.cartographer.core.elasticsearch.cartographer.*;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotService;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotServiceImpl;
import com.jahnelgroup.cartographer.core.event.Event;
import com.jahnelgroup.cartographer.core.event.EventServiceImpl;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.apache.ApacheHttpClient;
import com.jahnelgroup.cartographer.core.http.elasticsearch.DefaultRestHighLevelClient;
import com.jahnelgroup.cartographer.core.http.elasticsearch.RestHighLevelClientProvider;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import com.jahnelgroup.cartographer.core.migration.MigrationFilename;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import com.jahnelgroup.cartographer.core.migration.compare.DefaultMigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.compare.MigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.file.*;
import com.jahnelgroup.cartographer.core.util.DateTimeProvider;
import com.jahnelgroup.cartographer.core.util.DefaultDateTimeProvider;
import com.jahnelgroup.cartographer.core.util.DefaultObjectMapperProvider;
import com.jahnelgroup.cartographer.core.util.ObjectMapperProvider;
import com.jahnelgroup.cartographer.core.execute.ExecuteService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jahnelgroup.cartographer.core.execute.ExecuteContext.C;
import static com.jahnelgroup.cartographer.core.execute.ExecuteService.E;
import static com.jahnelgroup.cartographer.core.execute.ExecuteService.eventService;

@Log4j2
public class Cartographer {

    @Getter @Setter
    private MigrationFileLoader migrationFileLoader = new DefaultMigrationFileLoader();

    @Getter @Setter
    private MigrationAggregator migrationAggregator = new DefaultMigrationAggregator();

    @Getter @Setter
    private MigrationFilenameParser migrationFilenameParser = new DefaultMigrationFilenameParser();

    @Getter @Setter
    private CartographerMigrationDocumentIdGenerator cartographerMigrationDocumentIdGenerator = new DefaultCartographerMigrationDocumentIdGenerator();

    @Getter @Setter
    private CartographerMappingProvider cartographerMappingProvider = new DefaultCartographerMappingProvider();

    @Getter @Setter
    private JsonNodeToMigrationMetaInfoConverter jsonNodeToMigrationMetaInfoConverter = new DefaultJsonNodeToMigrationMetaInfoConverter();

    @Getter @Setter
    private MigrationEqualityProvider migrationEqualityProvider = new DefaultMigrationEqualityProvider();

    @Getter @Setter
    private ChecksumProvider checksumProvider = new DefaultChecksumProvider();

    @Getter @Setter
    private DateTimeProvider dateTimeProvider = new DefaultDateTimeProvider();

    @Getter @Setter
    private ObjectMapperProvider objectMapperProvider = new DefaultObjectMapperProvider();

    @Getter @Setter
    private ElasticsearchHttpClient httpClient = new ApacheHttpClient();

    @Getter @Setter
    private RestHighLevelClientProvider restHighLevelClientProvider = new DefaultRestHighLevelClient();

    @Getter @Setter
    private CartographerConfiguration config;

    private DocumentService documentService;
    private SnapshotService snapshotService;
    private IndexService indexService;
    private CartographerService cartographerService;

    public Cartographer() throws Exception {
        if( config == null ) config = new CartographerConfiguration();
    }

    /**
     * Initialize services and inject configuration into the providers and services.
     */
    protected void init() {
        ExecuteService.eventService = new EventServiceImpl();

        ConfigUtils.injectCartographerConfiguration(config,
                ExecuteService.eventService,
                migrationFileLoader,
                migrationFilenameParser,
                cartographerMigrationDocumentIdGenerator,
                cartographerMappingProvider,
                jsonNodeToMigrationMetaInfoConverter,
                migrationEqualityProvider,
                checksumProvider,
                dateTimeProvider,
                objectMapperProvider,
                httpClient,
                restHighLevelClientProvider);

        RestHighLevelClient restHighLevelClient = restHighLevelClientProvider.restHighLevelClient();

        documentService = new DocumentServiceImpl(
                config,
                objectMapperProvider.objectMapper(),
                restHighLevelClient);

        snapshotService = new SnapshotServiceImpl(
                config,
                httpClient);

        indexService = new IndexServiceImpl(
                config,
                restHighLevelClient,
                objectMapperProvider.objectMapper());

        cartographerService = new CartographerServiceImpl(
                config,
                documentService,
                indexService,
                cartographerMigrationDocumentIdGenerator,
                cartographerMappingProvider,
                jsonNodeToMigrationMetaInfoConverter,
                objectMapperProvider.objectMapper());
    }

    /**
     * Start auto migration of all indexes.
     *
     * @throws Exception
     */
    public void migrate() throws Exception {
        init();
        snapshot();
        createCartographerIndex();
        doMigrate();
    }

    /**
     * Takes a snapshot if the config property is set.
     *
     * @throws Exception
     */
    protected void snapshot() throws Exception {
        if( config.isTakeSnapshot() ){
            E(C("SNAPSHOT", () -> snapshotService.takeSnapshop()));
        }
    }

    /**
     * Creates Cartographer Index if it doesn't exist.
     *
     * @throws Exception
     */
    protected void createCartographerIndex() throws Exception {
        if (config.isClean() && cartographerService.indexExists() ){
            E(C("CLEAN", () -> {
                cartographerService.deleteIndex();
            }));
        }

        if( !cartographerService.indexExists() ){
            E(C("CREATE_SCHEMA", () -> {
                cartographerService.createIndex();
            }));
        }
    }

    /**
     * Performs the actual migration work.
     *
     * @throws Exception
     */
    protected void doMigrate() throws Exception {
        E(C("MIGRATION", () -> {
            Map<String, SortedSet<Migration>> migsOnDisk = loadMigrationsFromDisk();
            for (Map.Entry<String, SortedSet<Migration>> migs : migsOnDisk.entrySet()) {
                indexMigration(migs.getKey(), migs.getValue().toArray(new Migration[migs.getValue().size()]));
            }
        }));
    }

    protected void indexMigration(String index, Migration[] migsOnDisk) throws Exception {
       E(C("INDEX_MIGRATION", () -> {
           MigrationMetaInfo[] metaInfoOnES = loadMigrationsFromES(index);

           if( metaInfoOnES.length > migsOnDisk.length ){
               throw new CartographerException("More migrations exist in Elasticsearch="
                       + metaInfoOnES.length + " than on Disk=" + migsOnDisk.length + " for index="+index);
           }

           for(int i=0; i<migsOnDisk.length; i++){
               Migration migDisk = migsOnDisk[i];

               // Apply new migration
               if( i > metaInfoOnES.length - 1){
                   eachMigration(migDisk, false);
               }
               // process existing migration
               else{
                   if( config.isRepair() ){
                       autoRepair(index, metaInfoOnES, i, migDisk);
                   }
                   eachMigrationValidation(migsOnDisk[i], metaInfoOnES[i]);
               }
           }
       }));
    }

    private void autoRepair(String index, MigrationMetaInfo[] metaInfoOnES, int i, Migration migDisk) throws Exception {
        if( metaInfoOnES[i].getStatus() != MigrationMetaInfo.Status.SUCCESS ){
            E(C("EACH_AUTO_REPAIR", () -> {
                eachMigration(migDisk, true); // if no exception was thrown then it should have been inserted
                // now update the corresponding entry to run through the validation
                MigrationMetaInfo updatedMMI = cartographerService.fetchMigration(index, migDisk.getMetaInfo().getVersion());
                if( !updatedMMI.getIndex().equals(metaInfoOnES[i].getIndex()) ||
                        !updatedMMI.getFilename().equals(metaInfoOnES[i].getFilename()) ||
                        !updatedMMI.getVersion().equals(metaInfoOnES[i].getVersion()) ){
                    throw new CartographerException("Something we wrong when attempting to auto repair. updatedMMI="+updatedMMI);
                }else{
                    metaInfoOnES[i] = updatedMMI;
                }
            }).migration(migDisk));
        }
    }

    protected void eachMigrationValidation(Migration migrationDisk, MigrationMetaInfo metaOnES) throws Exception {
        E(C("EACH_MIGRATION_VALIDATION", () -> {
            if( metaOnES.getStatus() != MigrationMetaInfo.Status.SUCCESS ){
                throw new CartographerException("Migration validation failed because status is " + metaOnES.getStatus() + " in Elasticsearch.");
            }

            if(!migrationEqualityProvider.migrationsAreEqual(migrationDisk.getMetaInfo(), metaOnES)){
                throw new CartographerException("Migration validation failed equality migOnDisk="+migrationDisk.getMetaInfo()
                        +", migOnES="+metaOnES);
            }
        }).migration(migrationDisk));
    }

    protected void eachMigration(Migration migDisk, boolean isRepair) throws Exception {
        E(C("EACH_MIGRATION", () -> {
            if( !indexService.exists(migDisk.getMetaInfo().getIndex()) ){
                log.debug("Mapping for index {} does not exist, will create it.",
                    migDisk.getMetaInfo().getIndex());
            }

            // Set
            E(C("UPDATE_SCHEMA", () -> cartographerService.pending(migDisk.getMetaInfo(), isRepair)));

            E(C("UPSERT_INDEX", () -> indexService.upsertIndex(migDisk))
                .onFailure((e) -> cartographerService.failed(migDisk.getMetaInfo())));

            E(C("UPDATE_SCHEMA", () -> cartographerService.success(migDisk.getMetaInfo())));

        }).migration(migDisk));
    }

    protected MigrationMetaInfo[] loadMigrationsFromES(String index) throws Exception {
        SortedSet<MigrationMetaInfo> metaInfoOnES = cartographerService.fetchMigrations(index);
        MigrationMetaInfo[] info = metaInfoOnES.toArray(new MigrationMetaInfo[metaInfoOnES.size()]);
        eventService.raise(new Event(Event.Type.AFTER_LOAD_MIGRATIONS_FROM_ELASTICSEARCH).bag("size", info.length).bag("index", index));
        return info;
    }

    protected Map<String, SortedSet<Migration>> loadMigrationsFromDisk() throws Exception {
        List<MigrationFile> migFiles = migrationFileLoader.fetchMigrations();
        if( migFiles == null || migFiles.isEmpty() ){
            migFiles = new ArrayList<>();
        }
        eventService.raise(new Event(Event.Type.AFTER_LOAD_MIGRATIONS_FROM_DISK).bag("size", migFiles.size()));

        if( migFiles.isEmpty() ){
            return new HashMap<>();
        }

        List<MigrationFilename> migNames = migFiles.stream().map(MigrationFile::getFilename)
                .map(migrationFilenameParser::parse).collect(Collectors.toList());

        return combine(migFiles, migNames);
    }

    protected Map<String, SortedSet<Migration>> combine(List<MigrationFile> migFiles, List<MigrationFilename> migNames) throws Exception {
        List<Migration> migrations = new ArrayList<>();
        for (int i = 0; i < migFiles.size(); i++) {
            MigrationFile migFile = migFiles.get(i);
            MigrationFilename migName = migNames.get(i);

            MigrationMetaInfo metaInfo = new MigrationMetaInfo(
                migName.getIndex(),
                migFile.getFilename(),
                migName.getVersion(),
                migName.getDescription(),
                checksumProvider.getChecksum(migFile),
                dateTimeProvider.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
                MigrationMetaInfo.Status.NONE
            );

            migrations.add(new Migration(migFile, metaInfo));
        }
        return migrationAggregator.aggregate(migrations);
    }


}
