package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DefaultChecksumProvider implements ChecksumProvider {
    @Override
    public String getChecksum(MigrationFile migrationFile) throws Exception {
        return DigestUtils.md5Hex(Files.newInputStream(Paths.get(migrationFile.getFile().toURI())));
    }
}
