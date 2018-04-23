package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.MigrationFile;

import java.io.IOException;
import java.util.List;

public interface MigrationFileLoader {

    List<MigrationFile> fetchMigrations() throws IOException;

}
