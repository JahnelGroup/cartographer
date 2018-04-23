package com.jahnelgroup.cartographer.core.elasticsearch.config;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

@Data
public class DefaultRestHighLevelClient implements RestHighLevelClientProvider, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(cartographerConfiguration.buildHosts()));
    }

}
