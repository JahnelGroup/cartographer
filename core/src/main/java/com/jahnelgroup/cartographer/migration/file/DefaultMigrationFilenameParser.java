package com.jahnelgroup.cartographer.migration.file;

import com.jahnelgroup.cartographer.migration.MigrationFilename;

public class DefaultMigrationFilenameParser implements MigrationFilenameParser {

    @Override
    public MigrationFilename parse(String filename) {
        String[] splitName = filename.split("_");

        String index = splitName[0];
        Integer version = Integer.parseInt(splitName[1].substring(1));
        String description = splitName[2];

        return new MigrationFilename(index, version, description);
    }

}
