package com.jahnelgroup.cartographer.core;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigUtils;
import com.jahnelgroup.cartographer.core.http.elasticsearch.DefaultRestHighLevelClient;
import com.jahnelgroup.cartographer.core.http.elasticsearch.RestHighLevelClientProvider;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.schema.*;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotService;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotServiceImpl;
import com.jahnelgroup.cartographer.core.event.Event;
import com.jahnelgroup.cartographer.core.event.EventService;
import com.jahnelgroup.cartographer.core.event.EventServiceImpl;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.apache.ApacheHttpClient;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import com.jahnelgroup.cartographer.core.migration.MigrationFilename;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import com.jahnelgroup.cartographer.core.migration.compare.DefaultMigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.compare.MigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.file.*;
import com.jahnelgroup.cartographer.core.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jahnelgroup.cartographer.core.event.Event.Type.*;

@Slf4j
public class Cartographer {

    @Getter @Setter
    private MigrationFileLoader migrationFileLoader = new DefaultMigrationFileLoader();

    @Getter @Setter
    private MigrationFilenameParser migrationFilenameParser = new DefaultMigrationFilenameParser();

    @Getter @Setter
    private SchemaMigrationDocumentIdGenerator schemaMigrationDocumentIdGenerator = new DefaultSchemaMigrationDocumentIdGenerator();

    @Getter @Setter
    private SchemaMappingProvider schemaMappingProvider = new DefaultSchemaMappingProvider();

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

    private CartographerConfiguration config = new CartographerConfiguration();

    private EventService eventService;
    private DocumentService documentService;
    private SnapshotService snapshotService;
    private IndexService indexService;
    private SchemaService schemaService;

    private void init(){
        eventService = new EventServiceImpl();

        ConfigUtils.injectCartographerConfiguration(config,
                migrationFileLoader,
                migrationFilenameParser,
                schemaMigrationDocumentIdGenerator,
                schemaMappingProvider,
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
                httpClient,
                objectMapperProvider.objectMapper(),
                restHighLevelClient);

        snapshotService = new SnapshotServiceImpl(
                config,
                httpClient);

        indexService = new IndexServiceImpl(
                config,
                httpClient,
                objectMapperProvider.objectMapper(),
                restHighLevelClient);

        schemaService = new SchemaServiceImpl(
                config,
                documentService,
                indexService,
                schemaMigrationDocumentIdGenerator,
                schemaMappingProvider,
                jsonNodeToMigrationMetaInfoConverter,
                objectMapperProvider.objectMapper());
    }

    public void migrate() throws CartographerException {
        init();

        eventService.raise(new Event(BEFORE_MIGRATION));

        try{
            if( config.isTakeSnapshot() ){
                try{
                    eventService.raise(new Event(BEFORE_SNAPSHOT));
                    snapshotService.takeSnapshop();
                    eventService.raise(new Event(AFTER_SNAPSHOT));
                }catch(Exception e){
                    eventService.raise(new Event(AFTER_SNAPSHOT_ERROR).exception(e));
                    throw e;
                }
            }

            doMigrate();
        }catch(Exception e){
            eventService.raise(new Event(AFTER_MIGRATION_ERROR).exception(e));
            throw new CartographerException(e);
        }finally{
            eventService.raise(new Event(AFTER_MIGRATION));
        }
    }

    private void doMigrate() throws Exception {
        List<Migration> migsOnDisk = loadMigrationsFromDisk();
        if( migsOnDisk.isEmpty() ) return;

        List<MigrationMetaInfo> metaInfoOnES = new ArrayList<>();
        if( schemaService.exists() ){
            metaInfoOnES = loadMigrationMetaInfoFromES();
        }else{
            schemaService.create();
        }

        if( metaInfoOnES.size() > migsOnDisk.size() ){
            throw new CartographerException("OutOfSync: More migrations exist in Elasticsearch than on disk.");
        }

        for(int i=0; i<migsOnDisk.size(); i++){
            Migration migDisk = migsOnDisk.get(i);

            // new migration
            if( i > metaInfoOnES.size() - 1){
                applyNewMigration(migDisk);
            }

            // existing migration
            else{
                validateExistingMigrations(migsOnDisk.get(i).getMetaInfo(), metaInfoOnES.get(i));
            }
        }
    }

    private void validateExistingMigrations(MigrationMetaInfo metaOnDisk, MigrationMetaInfo metaOnES) throws CartographerException {
        if( metaOnES.getStatus() != MigrationMetaInfo.Status.SUCCESS ){
            throw new CartographerException("ExistingFailedMigration: Cannot migrate with an existing failed migration. metaOnES="+metaOnES);
        }

        if(!migrationEqualityProvider.migrationsAreEqual(metaOnDisk, metaOnES)){
            throw new CartographerException("OutOfSync: Existing migrations don't match up. migOnDisk="+metaOnDisk
                    +", migOnES="+metaOnES);
        }
    }

    private void applyNewMigration(Migration migDisk) throws Exception {
        eventService.raise(new Event(BEFORE_EACH_MIGRATION));

        if( indexService.exists(migDisk.getMetaInfo().getIndex()) ){
            log.debug("Mapping for index {} already exists, will migrate to the new version.",
                    migDisk.getMetaInfo().getIndex());
        }else{
            log.debug("Mapping for index {} does not exist, will index it.",
                    migDisk.getMetaInfo().getIndex());
        }

        eventService.raise(new Event(BEFORE_SCHEMA_CREATE));
        schemaService.index(migDisk.getMetaInfo());
        eventService.raise(new Event(AFTER_SCHEMA_CREATE));

        try{
            // apply the migration
            eventService.raise(new Event(BEFORE_PUT_MAPPING));
            indexService.putMapping(migDisk);
            schemaService.success(migDisk.getMetaInfo());
            eventService.raise(new Event(AFTER_PUT_MAPPING));

        }catch(Exception e){
            schemaService.failed(migDisk.getMetaInfo());
            eventService.raise(new Event(AFTER_PUT_MAPPING_ERROR));
            throw e;
        }

        eventService.raise(new Event(AFTER_EACH_MIGRATION));
    }

    private List<MigrationMetaInfo> loadMigrationMetaInfoFromES() {
        return schemaService.fetchMigrations();
    }

    private List<Migration> loadMigrationsFromDisk() throws Exception {
        List<MigrationFile> migFiles = migrationFileLoader.fetchMigrations();

        List<MigrationFilename> migNames = migFiles.stream().map(MigrationFile::getFile).map(File::getName)
                .map(migrationFilenameParser::parse).collect(Collectors.toList());

        return combine(migFiles, migNames);
    }

    private List<Migration> combine(List<MigrationFile> migFiles, List<MigrationFilename> migNames) throws Exception {
        List<Migration> migrations = new ArrayList<>();
        for (int i = 0; i < migFiles.size(); i++) {
            MigrationFile migFile = migFiles.get(i);
            MigrationFilename migName = migNames.get(i);

            MigrationMetaInfo metaInfo = new MigrationMetaInfo(
                migName.getIndex(),
                migFile.getFile().getName(),
                migName.getVersion(),
                migName.getDescription(),
                checksumProvider.getChecksum(migFile),
                dateTimeProvider.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
                MigrationMetaInfo.Status.NONE
            );

            migrations.add(new Migration(migFile, metaInfo));
        }
        return migrations;
    }


}
