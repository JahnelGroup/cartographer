package com.jahnelgroup.cartographer.core.migration.file;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.migration.MigrationFile;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
