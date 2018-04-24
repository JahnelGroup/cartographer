package com.jahnelgroup.cartographer.core.http;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;

@Data
public class DefaultHttpClientProvider implements HttpClientProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public ElasticsearchHttpClient httpClient() {
        // TODO:
        return null;
    }
}
