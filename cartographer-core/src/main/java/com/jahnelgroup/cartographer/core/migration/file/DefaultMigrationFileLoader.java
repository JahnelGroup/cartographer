package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
public class DefaultMigrationFileLoader implements MigrationFileLoader, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public List<MigrationFile> fetchMigrations() throws Exception {
        List<MigrationFile> mappings = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(cartographerConfiguration.getMigrationLocation());

        if( resource != null ){
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(resource.toURI()))) {
                for (Path path : directoryStream) {
                    String filename = cartographerConfiguration.getMigrationLocation() + "/" + path.getFileName();
                    String content = IOUtils.toString(classLoader.getResourceAsStream(filename), Charset.defaultCharset());
                    mappings.add(new MigrationFile(path.toString(), content));
                }
            }
        }

        return mappings;
    }

}
