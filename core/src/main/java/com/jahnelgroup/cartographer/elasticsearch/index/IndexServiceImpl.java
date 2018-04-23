package com.jahnelgroup.cartographer.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.cartographer.config.CartographerConfiguration;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class IndexServiceImpl implements IndexService {

    private RestTemplate restTemplate;
    private CartographerConfiguration elasticsearchProperties;

    @Override
    public JsonNodeIndex putMapping(String index, JsonNode indexMapping) {
        this.exchange("/" + index, HttpMethod.PUT, indexMapping);
        return this.findOne(index);
    }

    @Override
    public boolean mappingExists(String index){
        try {
            return this.exchange("/" + index, HttpMethod.GET, null).getStatusCode() == HttpStatus.OK;
        } catch (RuntimeException re){
            if( re.getCause() instanceof HttpClientErrorException ){
                HttpClientErrorException hcee = (HttpClientErrorException) re.getCause();
                if( hcee.getStatusCode() == HttpStatus.NOT_FOUND ){
                    return false;
                }
            }
            throw re;
        }
    }

    @Override
    public JsonNodeIndex findOne(String index) {
        return new JsonNodeIndex(
                this.exchange("/" + index, HttpMethod.GET, null).getBody(),
                index);
    }

    @Override
    public List<JsonNodeIndex> list() {
        final JsonNode node = this.exchange("/_cat/indices", HttpMethod.GET, null).getBody();
        final List<JsonNodeIndex> toRet = new ArrayList<>();
        if(node.isArray()) {
            final ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.forEach(element -> toRet.add(new JsonNodeIndex(element, element.get("index").asText())));
        }
        return toRet;
    }

    @Override
    public JsonNode delete(String index) {
        return this.exchange("/" + index, HttpMethod.DELETE, null).getBody();
    }

    private ResponseEntity<JsonNode> exchange(String url, HttpMethod method, JsonNode payload) {
        Exception lastException = null;
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<?> entity = payload == null
                ? new HttpEntity<String>(headers)
                : new HttpEntity<>(payload, headers);
        for(HttpHost host : elasticsearchProperties.buildHosts()) {
            try {
                return restTemplate.exchange(
                        host.toURI() + url,
                        method,
                        entity,
                        JsonNode.class);
            } catch(Exception e) {
                lastException = e;
            }
        }
        throw new RuntimeException(lastException);
    }

}