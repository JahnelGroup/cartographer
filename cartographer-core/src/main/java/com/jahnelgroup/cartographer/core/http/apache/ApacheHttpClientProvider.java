package com.jahnelgroup.cartographer.core.http.apache;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.HttpClientProvider;
import lombok.Data;

@Data
public class ApacheHttpClientProvider implements HttpClientProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public ElasticsearchHttpClient httpClient() {
        return new ApacheHttpClient();
    }

}
