package com.jahnelgroup.cartographer.core.elasticsearch.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private CartographerConfiguration cartographerConfiguration;
    private ElasticsearchHttpClient elasticsearchHttpClient;
    private ObjectMapper objectMapper;
    private RestHighLevelClient restHighLevelClient;

    public List<JsonNodeDocument> findAll(String index) {
        try {
            final QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            final SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(index);
            searchRequest.source(sourceBuilder);

            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            final SearchHits hits = searchResponse.getHits();
            final List<JsonNodeDocument> list = Arrays.stream(hits.getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(this::readTree)
                    .map(node -> new JsonNodeDocument(node, index, node.get("id").asText()))
                    .collect(Collectors.toList());
            return list;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument index(String index, String documentId, JsonNode document) {
        try {
            final String jsonString = objectMapper.writeValueAsString(document);
            restHighLevelClient.index(new IndexRequest(index, index, documentId).source(jsonString, XContentType.JSON));
            return new JsonNodeDocument(document, index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument update(String index, String documentId, JsonNode document) {
        try {
            final String jsonString = objectMapper.writeValueAsString(document);
            restHighLevelClient.update(new UpdateRequest(index, index, documentId).doc(jsonString, XContentType.JSON));
            return new JsonNodeDocument(document, index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode delete(String index, String documentId) {
        try {
            final DeleteResponse response = restHighLevelClient.delete(
                    new DeleteRequest(index, index, documentId));
            return objectMapper.convertValue(response, JsonNode.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


    private JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}