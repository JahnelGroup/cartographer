package com.jahnelgroup.cartographer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.elasticsearch.schema.SchemaService;
import com.jahnelgroup.cartographer.migration.compare.DefaultMigrationEqualityProvider;
import com.jahnelgroup.cartographer.migration.compare.MigrationEqualityProvider;
import com.jahnelgroup.cartographer.migration.file.DefaultMigrationFileLoader;
import com.jahnelgroup.cartographer.migration.file.DefaultMigrationFilenameParser;
import com.jahnelgroup.cartographer.elasticsearch.snapshot.SnapshotService;
import com.jahnelgroup.cartographer.util.*;

import com.jahnelgroup.cartographer.migration.Migration;
import com.jahnelgroup.cartographer.migration.MigrationFile;
import com.jahnelgroup.cartographer.migration.MigrationFilename;
import com.jahnelgroup.cartographer.migration.MigrationMetaInfo;
import com.jahnelgroup.cartographer.migration.file.MigrationFileLoader;
import com.jahnelgroup.cartographer.migration.file.MigrationFilenameParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cartographer {

    private MigrationFileLoader migrationFileLoader = new DefaultMigrationFileLoader();
    private MigrationFilenameParser migrationFilenameParser = new DefaultMigrationFilenameParser();
    private MigrationEqualityProvider migrationEqualityProvider = new DefaultMigrationEqualityProvider();

    private ChecksumProvider checksumProvider = new DefaultChecksumProvider();
    private DateTimeProvider dateTimeProvider = new DefaultDateTimeProvider();
    private ObjectMapperProvider objectMapperProvider = new DefaultObjectMapperProvider();

    private CartographerConfiguration config = new CartographerConfiguration();

    private IndexService indexService;
    private SchemaService schemaService;
    private SnapshotService snapshotService;

    public void migrate() throws CartographerException {
        try{
            if( config.isTakeSnapshot() ){
                snapshotService.takeSnapshop();
            }

            doMigrate();
        }catch(IOException e){
            throw new CartographerException(e);
        }
    }

    private void doMigrate() throws IOException, CartographerException {
        ObjectMapper objectMapper = objectMapperProvider.objectMapper();

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
                schemaService.create(migDisk);

                // apply the migration
                try{
                    indexService.putMapping(migDisk.getMetaInfo().getIndex(),
                            objectMapper.readTree(migDisk.getMigrationFile().getContents()));
                    schemaService.success(migDisk);
                }catch(Exception e){
                    schemaService.failed(migDisk);
                }

            }

            // existing migration
            else{
                Migration migOnES = migsOnES.get(i);

                if( migOnES.getMetaInfo().getStatus() != MigrationMetaInfo.Status.SUCCESS ){
                    throw new CartographerException("ExistingFailedMigration: Cannot migrate with an existing failed migration. migsOnES="+migsOnES);
                }

                if(!migrationEqualityProvider.migrationsAreEqual(migDisk, migOnES)){
                    throw new CartographerException("OutOfSync: Existing migrations don't match up. migsOnDisk="+migsOnDisk
                            +", migsOnES="+migsOnES);
                }
            }
        }
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
