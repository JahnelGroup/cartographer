package com.jahnelgroup.cartographer.core.elasticsearch.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
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
    private ObjectMapper objectMapper;
    private RestHighLevelClient restHighLevelClient;

    public List<JsonNodeDocument> findAll(String index) {
        try {
            final QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            final SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(sourceBuilder);

            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = searchResponse.getHits();

            return Arrays.stream(hits.getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(this::readTree)
                    .map(node -> new JsonNodeDocument(node, index, node.get("documentId").asText()))
                    .collect(Collectors.toList());

        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument index(String index, String documentId, JsonNode document) throws CartographerException{
        try {
            final String jsonString = objectMapper.writeValueAsString(document);

            IndexRequest request = new IndexRequest(index).id(documentId).source(jsonString, XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

            if( response.status().getStatus() != 200 && response.status().getStatus() != 201 ){
                throw new CartographerException(index, "unable to index document="+document
                        +". Expected status 200 or 201 but received " + response.status().getStatus());
            }

            return new JsonNodeDocument(document, index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument update(String index, String documentId, JsonNode document) throws CartographerException {
        try {
            final String jsonString = objectMapper.writeValueAsString(document);

            UpdateRequest request = new UpdateRequest(index, documentId).doc(jsonString, XContentType.JSON);
            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);

            if( response.status().getStatus() != 200 && response.status().getStatus() != 201 ){
                throw new CartographerException(index, "unable to index document="+document
                        +". Expected status 200 or 201 but received " + response.status().getStatus());
            }

            return new JsonNodeDocument(document, index, documentId);
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
