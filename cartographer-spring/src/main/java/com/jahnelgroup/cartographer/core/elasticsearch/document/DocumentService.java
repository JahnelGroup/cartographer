package com.jahnelgroup.cartographer.core.elasticsearch.document;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface DocumentService {

    List<JsonNodeDocument> findAll(String index);
    JsonNodeDocument index(String index, String documentId, JsonNode document);
    JsonNodeDocument update(String index, String documentId, JsonNode document);
    JsonNode delete(String index, String documentId);

}