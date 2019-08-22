package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Data
public class DefaultMigrationFileLoader implements MigrationFileLoader, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;
    private ChecksumProvider checksumProvider;

    @Override
    public List<MigrationFile> fetchMigrations() throws Exception {
        List<MigrationFile> mappings = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forResource(cartographerConfiguration.getMigrationLocation()))
                    .setScanners(new ResourcesScanner())
        );

        Set<String> filenames = reflections.getResources(Pattern.compile(".*\\.json"));

        for(String filename: filenames){
            String content = IOUtils.toString(classLoader.getResourceAsStream(filename), Charset.defaultCharset());
            mappings.add(new MigrationFile(
                    Paths.get(filename).getFileName().toString(),
                    content));
        }

        return mappings;
    }

}
