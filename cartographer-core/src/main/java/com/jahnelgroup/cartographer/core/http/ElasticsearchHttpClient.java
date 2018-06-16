package com.jahnelgroup.cartographer.core.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;

public interface ElasticsearchHttpClient {

    enum HttpMethod{
        GET, PUT, DELETE, POST
    }

    @Data
    @AllArgsConstructor
    @Accessors(fluent = true)
    class HttpRequest {
        private String url;
        private ElasticsearchHttpClient.HttpMethod method;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @Accessors(fluent = true)
    class HttpResponse {
        private int status;
        private String content;
    }

    HttpResponse exchange(HttpRequest request) throws IOException;

}
