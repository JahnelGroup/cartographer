package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpRequest;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpResponse;
import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod.*;

@Data
@AllArgsConstructor
public class IndexServiceImpl implements IndexService {

    private CartographerConfiguration cartographerConfiguration;
    private ElasticsearchHttpClient elasticsearchHttpClient;
    private ObjectMapper objectMapper;
    private RestHighLevelClient restHighLevelClient;

    @Override
    public JsonNodeIndex putMapping(Migration migration) throws IOException {
        return putMapping(migration.getMetaInfo().getIndex(), migration.getMigrationFile().getContents());
    }

    @Override
    public JsonNodeIndex putMapping(String index, String mapping) throws IOException {
        if( !exists(index) ){
            createIndex(index);
        }

        HttpResponse resp = elasticsearchHttpClient.exchange(request(index, PUT, mapping));

        if( resp.status() != 200 ){
            throw new IOException("Did not successfully put the mapping for index="+index+". received http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        return this.findOne(index);
    }

    private void createIndex(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(new HttpRequest(getHost() + "/" + index ,
                PUT, ""));

        if( resp.status() != 200 ){
            throw new IOException("Did not successfully createIndex index="+index+". received http status code = "
                    + resp.status() + ", content = " + resp.content());
        }
    }

    @Override
    public boolean exists(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(new HttpRequest(getHost() + "/" + index, GET, null));
        switch(resp.status()){
            case 200: return true;
            case 404: return false;
            default: throw new IOException("Did not successfully detect the existence of index="+index+". received http status code = "
                    + resp.status() + ", content = " + resp.content());
        }
    }

    @Override
    public JsonNodeIndex findOne(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(request(index, GET, null));

        if( resp.status() != 200 ){
            throw new IOException("Unable to retrieve index="+index+". http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        return new JsonNodeIndex(objectMapper.readTree(resp.content()), index);
    }

    @Override
    public List<JsonNodeIndex> list() throws IOException {

        HttpResponse resp = elasticsearchHttpClient.exchange(request(cartographerConfiguration.getElasticsearchListIndexesUri(),
                GET, null));

        if( resp.status() != 200 ){
            throw new IOException("Unable to retrieve list indexes. http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        final JsonNode node = objectMapper.readTree(resp.content());

        final List<JsonNodeIndex> toRet = new ArrayList<>();
        if(node.isArray()) {
            final ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.forEach(element -> toRet.add(new JsonNodeIndex(element, element.get("index").asText())));
        }
        return toRet;
    }

    @Override
    public JsonNode delete(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(request(index, DELETE, null));

        if( resp.status() != 200 ){
            throw new IOException("Unable to delete index="+index+". http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        return objectMapper.readTree(resp.content());
    }

    private HttpRequest request(String index, HttpMethod method, String content){
        return new HttpRequest(getHost() + "/" + index + "/_mapping/" + index,
                method, content);
    }

    private String getHost() {
        String host = cartographerConfiguration.getClusterNodes();
        if ( !host.startsWith("http") ){
            host = "http://" + host;
        }
        return host;
    }
}