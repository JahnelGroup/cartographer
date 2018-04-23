package com.jahnelgroup.cartographer.core.http;

import com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient;

public interface HttpClientProvider {

    ElasticsearchHttpClient httpClient();

}
