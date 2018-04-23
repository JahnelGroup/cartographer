package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.JsonNodeDocument;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo.Status.*;

@Data
@AllArgsConstructor
public class SchemaServiceImpl implements SchemaService {

    private CartographerConfiguration cartographerConfiguration;
    private DocumentService documentService;
    private SchemaMigrationDocumentIdGenerator schemaMigrationDocumentIdProvider;
    private JsonNodeToMigrationMetaInfoConverter jsonNodeToMigrationMetaInfoConverter;
    private ObjectMapper objectMapper;

    @Override
    public List<MigrationMetaInfo> fetchMigrations() {
        return documentService.findAll(cartographerConfiguration.getSchemaIndex())
            .stream()
                .map(JsonNodeDocument::getJsonNode)
                .map(jsonNodeToMigrationMetaInfoConverter::convert)
                .collect(Collectors.toList())
            ;
    }

    @Override
    public void create(MigrationMetaInfo metaInfa) throws IOException {
        metaInfa.setStatus(PENDING);

        documentService.index(cartographerConfiguration.getSchemaIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfa),
                getJsonNode(metaInfa));
    }

    private JsonNode getJsonNode(MigrationMetaInfo metaInfa) throws IOException {
        return objectMapper.readTree(objectMapper.writeValueAsBytes(metaInfa));
    }

    @Override
    public void success(Migration migDisk) {
        MigrationMetaInfo metaInfa = migDisk.getMetaInfo();
        metaInfa.setStatus(SUCCESS);
    }

    @Override
    public void failed(Migration migDisk) {
        MigrationMetaInfo metaInfa = migDisk.getMetaInfo();
        metaInfa.setStatus(FAILED);
    }
}
