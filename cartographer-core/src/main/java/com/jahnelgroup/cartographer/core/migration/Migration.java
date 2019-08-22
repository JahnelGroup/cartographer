package com.jahnelgroup.cartographer.core.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class Migration {

    private MigrationFile migrationFile;
    private MigrationMetaInfo metaInfo;

    public MigrationContent toJson(ObjectMapper objectMapper) throws IOException {
        String index = getMetaInfo().getIndex();
        String contents = getMigrationFile().getContents();
        JsonNode payload = objectMapper.readTree(contents);
        String settings = payload.has("settings") ? payload.get("settings").toString() : "";
        String mappings = payload.has("mappings") ? payload.get("mappings").toString() : "";

        return new MigrationContent(index, settings, mappings);
    }

}
