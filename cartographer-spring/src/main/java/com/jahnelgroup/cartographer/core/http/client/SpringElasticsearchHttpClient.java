package com.jahnelgroup.cartographer.core.http.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class SpringElasticsearchHttpClient implements ConfigurationAware {

    @Getter @Setter
    private RestTemplate restTemplate;

    @Setter
    private CartographerConfiguration cartographerConfiguration;

    public SpringElasticsearchHttpClient(){
        this.restTemplate = new RestTemplate();
    }

    public JsonNode exchange(String url, ElasticsearchHttpClient.HttpMethod method, JsonNode payload) {
        Exception lastException = null;
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<?> entity = payload == null
                ? new HttpEntity<String>(headers)
                : new HttpEntity<>(payload, headers);
        for(HttpHost host : cartographerConfiguration.buildHosts()) {
            try {
                return restTemplate.exchange(
                        host.toURI() + url,
                        mapHttpMethod(method),
                        entity,
                        JsonNode.class).getBody();
            } catch(Exception e) {
                lastException = e;
            }
        }
        throw new RuntimeException(lastException);
    }

    private HttpMethod mapHttpMethod(ElasticsearchHttpClient.HttpMethod method){
        switch(method){
            case GET: return HttpMethod.GET;
            case PUT: return HttpMethod.PUT;
            case DELETE: return HttpMethod.DELETE;
            default: throw new UnsupportedOperationException("HttpMethod " + method + " is not supported by " + this);
        }
    }


}
