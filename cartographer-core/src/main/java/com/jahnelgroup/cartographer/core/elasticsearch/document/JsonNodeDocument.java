package com.jahnelgroup.cartographer.core.elasticsearch.document;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonNodeDocument {

    private JsonNode jsonNode;
    private String index;
    private String documentId;

}