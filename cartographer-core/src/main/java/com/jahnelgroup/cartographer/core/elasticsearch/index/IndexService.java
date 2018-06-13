package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.migration.Migration;

import java.io.IOException;
import java.util.List;

public interface IndexService {

    boolean exists(String index) throws IOException;
    JsonNodeIndex putIndex(Migration migration) throws IOException, CartographerException;
    JsonNodeIndex putIndex(String index, String mapping) throws IOException, CartographerException;
    JsonNodeIndex findOne(String index) throws IOException;
    List<JsonNodeIndex> list() throws IOException;
    JsonNode deleteMapping(String index) throws IOException;
    JsonNode deleteIndex(String index) throws IOException;

}
