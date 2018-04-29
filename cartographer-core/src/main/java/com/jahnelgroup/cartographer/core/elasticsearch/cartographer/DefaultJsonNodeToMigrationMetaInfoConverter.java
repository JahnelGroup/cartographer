package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.fasterxml.jackson.databind.JsonNode;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public class DefaultJsonNodeToMigrationMetaInfoConverter implements JsonNodeToMigrationMetaInfoConverter {

    @Override
    public MigrationMetaInfo convert(JsonNode jsonNode) {
        return new MigrationMetaInfo(
                jsonNode.get("index").textValue(),
                jsonNode.get("filename").textValue(),
                jsonNode.get("version").intValue(),
                jsonNode.get("description").textValue(),
                jsonNode.get("checksum").textValue(),
                jsonNode.get("timestamp").textValue(),
                MigrationMetaInfo.Status.valueOf(jsonNode.get("status").textValue())
        );
    }

}
