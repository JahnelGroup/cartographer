package com.jahnelgroup.cartographer.elasticsearch.index;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JsonNodeIndex {

    private String id;
    private JsonNode document;

    public JsonNodeIndex(JsonNode document, String index) {
        this.document = document;
        this.id = index;
    }

}