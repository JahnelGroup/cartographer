package com.jahnelgroup.cartographer.core.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;

public interface RestHighLevelClientProvider {

    RestHighLevelClient restHighLevelClient();

}
