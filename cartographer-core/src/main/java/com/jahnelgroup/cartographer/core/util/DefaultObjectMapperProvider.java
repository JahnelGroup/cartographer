package com.jahnelgroup.cartographer.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultObjectMapperProvider implements ObjectMapperProvider {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

}
