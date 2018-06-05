package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
public class DefaultChecksumProvider implements ChecksumProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public String getChecksum(MigrationFile migrationFile) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String filename = cartographerConfiguration.getMigrationLocation() + "/" + migrationFile.getFilename();
        return DigestUtils.md5Hex(classLoader.getResourceAsStream(filename));
    }
}
