package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.migration.Migration;

import java.io.IOException;
import java.util.List;

public interface IndexService {

    boolean mappingExists(String index);
    JsonNodeIndex putMapping(Migration migration) throws IOException;
    JsonNodeIndex findOne(String index);
    List<JsonNodeIndex> list();
    JsonNode delete(String index);

}
