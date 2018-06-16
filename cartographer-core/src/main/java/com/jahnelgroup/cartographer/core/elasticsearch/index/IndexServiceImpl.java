package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpRequest;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpResponse;
import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod.DELETE;
import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod.GET;
import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod.POST;
import static com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient.HttpMethod.PUT;

@Data
@AllArgsConstructor
public class IndexServiceImpl implements IndexService {

    private CartographerConfiguration cartographerConfiguration;
    private ElasticsearchHttpClient elasticsearchHttpClient;
    private ObjectMapper objectMapper;

    @Override
    public JsonNodeIndex putIndex(Migration migration) throws IOException, CartographerException {
        return putIndex(migration.getMetaInfo().getIndex(), migration.getMigrationFile().getContents());
    }

    @Override
    public JsonNodeIndex putIndex(String index, String content) throws CartographerException, IOException {
        if( !exists(index) ){
            createIndex(index);
        }

        JsonNode payload = this.objectMapper.readTree(content);

        if (payload.has("settings")) {
            closeIndex(index);
            putSettings(index, payload.get("settings").toString());
            openIndex(index);
        }

        if (payload.has("mappings") && payload.get("mappings").has(index)) {
            putMappings(index, payload.get("mappings").get(index).toString());
        }

        return this.findOne(index);
    }

    private void putSettings(String index, String settings) throws CartographerException, IOException {

        HttpResponse settingsResp = elasticsearchHttpClient.exchange(request(index + "/_settings/", PUT, settings));

        if( settingsResp.status() != 200 ){
            throw new CartographerException(index, "unable to put the settings. Received http status code = "
                + settingsResp.status() + ", content = " + settingsResp.content());
        }

    }

    private void putMappings(String index, String mappings) throws CartographerException, IOException {

        HttpResponse resp = elasticsearchHttpClient.exchange(request(index + "/_mappings/" + index, PUT, mappings));

        if( resp.status() != 200 ){
            throw new CartographerException(index, " unable to put the mapping. Received http status code = "
                + resp.status() + ", content = " + resp.content());
        }

    }

    @Override
    public void closeIndex(String index) throws IOException, CartographerException {
        HttpResponse resp = elasticsearchHttpClient.exchange(request(index + "/_close/", POST, ""));

        if( resp.status() != 200 ){
            throw new CartographerException(index, " unable to close the index. Received http status code = "
                + resp.status() + ", content = " + resp.content());
        }
    }

    @Override
    public void openIndex(String index) throws IOException, CartographerException {
        HttpResponse resp = elasticsearchHttpClient.exchange(request(index + "/_open/", POST, ""));

        if( resp.status() != 200 ){
            throw new CartographerException(index, " unable to open the index. Received http status code = "
                + resp.status() + ", content = " + resp.content());
        }
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
    public JsonNode deleteIndex(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(new HttpRequest(getHost() + "/" + index ,
                DELETE, ""));

        if( resp.status() != 200 ){
            throw new IOException("Unable to delete index="+index+". http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        return objectMapper.readTree(resp.content());
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

        HttpResponse resp = elasticsearchHttpClient.exchange(new HttpRequest(getHost() + "/_cat/indices?format=json",
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
    public JsonNode deleteMapping(String index) throws IOException {
        HttpResponse resp = elasticsearchHttpClient.exchange(request(index, DELETE, null));

        if( resp.status() != 200 ){
            throw new IOException("Unable to delete index="+index+" mapping. http status code = "
                    + resp.status() + ", content = " + resp.content());
        }

        return objectMapper.readTree(resp.content());
    }

    private HttpRequest request(String index, HttpMethod method, String content){
        return new HttpRequest(getHost() + "/" + index,
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
