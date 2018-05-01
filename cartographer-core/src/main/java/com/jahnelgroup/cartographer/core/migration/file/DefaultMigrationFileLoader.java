package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class DefaultMigrationFileLoader implements MigrationFileLoader, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public List<MigrationFile> fetchMigrations() throws Exception {
        List<MigrationFile> mappings = new ArrayList<>();

        for (File file : getResourceFolderFiles(cartographerConfiguration.getMigrationLocation())) {
            String contents = FileUtils.readFileToString(file, Charset.defaultCharset());
            mappings.add(new MigrationFile(file, contents));
        }

        return mappings;
    }

    private File[] getResourceFolderFiles (String folder) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(folder);
        if( resource == null ){
            return new File[0];
        }
        File file = new File(resource.getFile());
        return file.listFiles();
    }

}
