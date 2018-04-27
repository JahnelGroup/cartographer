package com.jahnelgroup.cartographer.core.elasticsearch.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.CartographerException;

import java.util.List;

public interface DocumentService {

    List<JsonNodeDocument> findAll(String index);
    JsonNodeDocument index(String index, String documentId, JsonNode document) throws CartographerException;
    JsonNodeDocument update(String index, String documentId, JsonNode document) throws CartographerException;

}