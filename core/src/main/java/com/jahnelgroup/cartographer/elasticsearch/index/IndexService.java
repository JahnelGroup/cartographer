package com.jahnelgroup.cartographer.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface IndexService {

    boolean mappingExists(String index);
    JsonNodeIndex putMapping(String index, JsonNode indexMapping);
    JsonNodeIndex findOne(String index);
    List<JsonNodeIndex> list();
    JsonNode delete(String index);

}
