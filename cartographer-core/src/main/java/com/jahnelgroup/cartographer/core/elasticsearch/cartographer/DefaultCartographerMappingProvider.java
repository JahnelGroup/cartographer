package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Data
public class DefaultCartographerMappingProvider implements CartographerMappingProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public String mapping() throws IOException {
        return IOUtils.toString(getResourceStream(cartographerConfiguration.getCartographerIndexMappingFile()),
            Charset.defaultCharset());
    }

    private InputStream getResourceStream (String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(file);
    }
}
