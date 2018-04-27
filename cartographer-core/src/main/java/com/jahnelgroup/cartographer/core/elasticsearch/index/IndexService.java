package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.migration.Migration;

import java.io.IOException;
import java.util.List;

public interface IndexService {

    boolean exists(String index) throws IOException;
    JsonNodeIndex putMapping(Migration migration) throws IOException;
    JsonNodeIndex putMapping(String index, String mapping) throws IOException;
    JsonNodeIndex findOne(String index) throws IOException;
    List<JsonNodeIndex> list() throws IOException;
    JsonNode delete(String index) throws IOException;

}
