package com.jahnelgroup.cartographer.core.http.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface ElasticsearchHttpClient {

    enum HttpMethod{
        GET, PUT, DELETE
    }

    JsonNode exchange(String url, ElasticsearchHttpClient.HttpMethod method, JsonNode payload);

}
