package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Data
public class DefaultSchemaMappingProvider implements SchemaMappingProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public String mapping() throws IOException {
        return FileUtils.readFileToString(getResourceFile(
                cartographerConfiguration.getSchemaMappingFile()),
                Charset.defaultCharset());
    }

    private File getResourceFile (String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(file).getFile());
    }
}
