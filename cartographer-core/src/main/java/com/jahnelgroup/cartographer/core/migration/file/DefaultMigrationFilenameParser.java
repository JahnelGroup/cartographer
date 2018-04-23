package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.MigrationFilename;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class DefaultMigrationFilenameParser implements MigrationFilenameParser {

    @Override
    public MigrationFilename parse(String filename) {
        String[] splitName = FilenameUtils.removeExtension(filename).split("_");
        splitName[1] = splitName[1].replaceAll("[^\\d.]", "");

        String index        = splitName[0];
        Integer version     = Integer.parseInt(splitName[1]);
        String description  = splitName[2];

        return new MigrationFilename(index, version, description);
    }

}
