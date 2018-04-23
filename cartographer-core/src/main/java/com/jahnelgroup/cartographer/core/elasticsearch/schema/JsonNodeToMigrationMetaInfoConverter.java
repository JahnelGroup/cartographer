package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public interface JsonNodeToMigrationMetaInfoConverter {

    MigrationMetaInfo convert(JsonNode jsonNode);

}
