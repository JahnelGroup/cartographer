package com.jahnelgroup.cartographer.migration.file;

import com.jahnelgroup.cartographer.migration.MigrationFile;

import java.io.IOException;
import java.util.List;

public interface MigrationFileLoader {

    List<MigrationFile> fetchMigrations() throws IOException;

}
