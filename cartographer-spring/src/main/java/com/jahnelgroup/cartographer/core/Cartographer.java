package com.jahnelgroup.cartographer.core;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.elasticsearch.config.DefaultRestHighLevelClient;
import com.jahnelgroup.cartographer.core.elasticsearch.config.RestHighLevelClientProvider;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.schema.DefaultSchemaMigrationDocumentIdGenerator;
import com.jahnelgroup.cartographer.core.elasticsearch.schema.SchemaMigrationDocumentIdGenerator;
import com.jahnelgroup.cartographer.core.elasticsearch.schema.SchemaService;
import com.jahnelgroup.cartographer.core.elasticsearch.schema.SchemaServiceImpl;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotService;
import com.jahnelgroup.cartographer.core.elasticsearch.snapshot.SnapshotServiceImpl;
import com.jahnelgroup.cartographer.core.event.Event;
import com.jahnelgroup.cartographer.core.event.EventService;
import com.jahnelgroup.cartographer.core.http.DefaultHttpClientProvider;
import com.jahnelgroup.cartographer.core.http.HttpClientProvider;
import com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.client.SpringElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import com.jahnelgroup.cartographer.core.migration.MigrationFilename;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import com.jahnelgroup.cartographer.core.migration.compare.DefaultMigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.compare.MigrationEqualityProvider;
import com.jahnelgroup.cartographer.core.migration.file.DefaultMigrationFileLoader;
import com.jahnelgroup.cartographer.core.migration.file.DefaultMigrationFilenameParser;
import com.jahnelgroup.cartographer.core.migration.file.MigrationFileLoader;
import com.jahnelgroup.cartographer.core.migration.file.MigrationFilenameParser;
import com.jahnelgroup.cartographer.core.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
    private MigrationEqualityProvider migrationEqualityProvider = new DefaultMigrationEqualityProvider();

    @Getter @Setter
    private ChecksumProvider checksumProvider = new DefaultChecksumProvider();

    @Getter @Setter
    private DateTimeProvider dateTimeProvider = new DefaultDateTimeProvider();

    @Getter @Setter
    private ObjectMapperProvider objectMapperProvider = new DefaultObjectMapperProvider();

    @Getter @Setter
    private HttpClientProvider httpClientProvider = new DefaultHttpClientProvider();

    @Getter @Setter
    private RestHighLevelClientProvider restHighLevelClientProvider = new DefaultRestHighLevelClient();


    private CartographerConfiguration config = new CartographerConfiguration();




    private EventService eventService;
    private DocumentService documentService;
    private IndexService indexService;
    private SchemaService schemaService;
    private SnapshotService snapshotService;

    private void beforeMigrate(){
        eventService.raise(new Event(BEFORE_MIGRATION));

        // TODO: Inject configuration into the providers.

        documentService = new DocumentServiceImpl(
                httpClientProvider.httpClient(),
                objectMapperProvider.objectMapper(),
                restHighLevelClientProvider.restHighLevelClient());

        snapshotService = new SnapshotServiceImpl(httpClientProvider.httpClient());

        indexService = new IndexServiceImpl(
                httpClientProvider.httpClient(),
                objectMapperProvider.objectMapper());

        schemaService = new SchemaServiceImpl(
                documentService,
                schemaMigrationDocumentIdGenerator,
                objectMapperProvider.objectMapper());
    }

    public void migrate() throws CartographerException {
        beforeMigrate();

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
            afterMigrate();
        }
    }

    private void afterMigrate(){
        eventService.raise(new Event(AFTER_MIGRATION));
    }

    private void doMigrate() throws Exception {
        List<Migration> migsOnDisk = loadMigrationsFromDisk();
        if( migsOnDisk.isEmpty() ) return;

        List<Migration> migsOnES = loadMigrationsFromES();

        if( migsOnES.size() > migsOnDisk.size() ){
            throw new CartographerException("OutOfSync: More migrations exist in Elasticsearch than on disk.");
        }

        for(int i=0; i<migsOnDisk.size(); i++){
            Migration migDisk = migsOnDisk.get(i);

            // new migration
            if( i >= migsOnES.size() - 1){
                applyNewMigration(migDisk);
            }

            // existing migration
            else{
                validateExistingMigrations(migsOnDisk.get(i), migsOnES.get(i));
            }
        }
    }

    private void validateExistingMigrations(Migration migOnDisk, Migration migOnES) throws CartographerException {
        if( migOnES.getMetaInfo().getStatus() != MigrationMetaInfo.Status.SUCCESS ){
            throw new CartographerException("ExistingFailedMigration: Cannot migrate with an existing failed migration. migOnES="+migOnES);
        }

        if(!migrationEqualityProvider.migrationsAreEqual(migOnDisk, migOnES)){
            throw new CartographerException("OutOfSync: Existing migrations don't match up. migOnDisk="+migOnDisk
                    +", migOnES="+migOnES);
        }
    }

    private void applyNewMigration(Migration migDisk) throws Exception {
        eventService.raise(new Event(BEFORE_EACH_MIGRATION));

        if( indexService.mappingExists(migDisk.getMetaInfo().getIndex()) ){
            log.debug("Mapping for index {} already exists, will migrate to the new version.",
                    migDisk.getMetaInfo().getIndex());
        }else{
            log.debug("Mapping for index {} does not exist, will create it.",
                    migDisk.getMetaInfo().getIndex());
        }

        eventService.raise(new Event(BEFORE_SCHEMA_CREATE));
        schemaService.create(migDisk);
        eventService.raise(new Event(AFTER_SCHEMA_CREATE));

        try{
            // apply the migration
            eventService.raise(new Event(BEFORE_PUT_MAPPING));
            indexService.putMapping(migDisk);
            schemaService.success(migDisk);
            eventService.raise(new Event(AFTER_PUT_MAPPING));

        }catch(Exception e){
            schemaService.failed(migDisk);
            eventService.raise(new Event(AFTER_PUT_MAPPING_ERROR));
            throw e;
        }

        eventService.raise(new Event(AFTER_EACH_MIGRATION));
    }

    private List<Migration> loadMigrationsFromES() {
        return schemaService.fetchMigrations();
    }

    private List<Migration> loadMigrationsFromDisk() throws IOException {
        List<MigrationFile> migFiles = migrationFileLoader.fetchMigrations();

        List<MigrationFilename> migNames = migFiles.stream().map(MigrationFile::getFilename)
                .map(migrationFilenameParser::parse).collect(Collectors.toList());

        return combine(migFiles, migNames);
    }

    private List<Migration> combine(List<MigrationFile> migFiles, List<MigrationFilename> migNames) {
        List<Migration> migrations = new ArrayList<>();
        for (int i = 0; i < migFiles.size(); i++) {
            MigrationFile migFile = migFiles.get(i);
            MigrationFilename migName = migNames.get(i);

            MigrationMetaInfo metaInfo = new MigrationMetaInfo(
                migName.getIndex(),
                migName.getVersion(),
                migName.getDescription(),
                checksumProvider.getChecksum(migFile.getContents()),
                dateTimeProvider.now()
            );

            migrations.add(new Migration(migFile, metaInfo));
        }
        return migrations;
    }


}
