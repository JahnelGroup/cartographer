package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient;
import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient.HttpMethod.*;

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
    public boolean mappingExists(String index) throws IOException {
        return restHighLevelClient.exists(new GetRequest().index(index));
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