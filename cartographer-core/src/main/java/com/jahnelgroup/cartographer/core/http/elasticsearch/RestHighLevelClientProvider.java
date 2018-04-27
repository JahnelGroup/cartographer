package com.jahnelgroup.cartographer.core.http.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

public interface RestHighLevelClientProvider {

    RestHighLevelClient restHighLevelClient();

}
