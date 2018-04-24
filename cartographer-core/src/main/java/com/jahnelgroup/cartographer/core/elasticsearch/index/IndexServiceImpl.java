package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;

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
        String index = migration.getMetaInfo().getIndex();
        elasticsearchHttpClient.exchange("/" + index, PUT,
                objectMapper.readTree(migration.getMigrationFile().getContents()));
        return this.findOne(index);
    }

    @Override
    public JsonNodeIndex putMapping(String index, String mapping) throws IOException {
        elasticsearchHttpClient.exchange("/" + index, PUT, objectMapper.readTree(mapping));
        return this.findOne(index);
    }

    @Override
    public boolean exists(String index) throws IOException {
        try{
            final QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            final SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(index);
            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            return searchResponse.getHits().totalHits > 0L;
        }catch(ElasticsearchStatusException e){
            if( RestStatus.NOT_FOUND.equals(e.status()) ){
                return false;
            }else{
                throw e;
            }
        }
    }

    @Override
    public JsonNodeIndex findOne(String index) {
        return new JsonNodeIndex(
                elasticsearchHttpClient.exchange("/" + index, GET, null), index);
    }

    @Override
    public List<JsonNodeIndex> list() {
        final JsonNode node = elasticsearchHttpClient.exchange(
                cartographerConfiguration.getElasticsearchListIndexesUri(), GET, null);

        final List<JsonNodeIndex> toRet = new ArrayList<>();
        if(node.isArray()) {
            final ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.forEach(element -> toRet.add(new JsonNodeIndex(element, element.get("index").asText())));
        }
        return toRet;
    }

    @Override
    public JsonNode delete(String index) {
        return elasticsearchHttpClient.exchange("/" + index, DELETE, null);
    }

}