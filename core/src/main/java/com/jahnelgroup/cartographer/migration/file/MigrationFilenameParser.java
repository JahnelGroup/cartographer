package com.jahnelgroup.cartographer.migration.file;

import com.jahnelgroup.cartographer.migration.MigrationFilename;

public interface MigrationFilenameParser {

    MigrationFilename parse(String filename);

}
