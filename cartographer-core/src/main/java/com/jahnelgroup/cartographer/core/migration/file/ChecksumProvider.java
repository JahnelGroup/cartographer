package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.MigrationFile;

public interface ChecksumProvider {

    String getChecksum(MigrationFile migrationFile) throws Exception;

}
