package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.MigrationFilename;

public interface MigrationFilenameParser {

    MigrationFilename parse(String filename);

}
