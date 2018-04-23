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
    public void create(MigrationMetaInfo metaInfo) throws IOException {
        metaInfo.setStatus(PENDING);
        documentService.index(cartographerConfiguration.getSchemaIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    @Override
    public void success(MigrationMetaInfo metaInfo) throws IOException {
        metaInfo.setStatus(SUCCESS);
        documentService.update(cartographerConfiguration.getSchemaIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    @Override
    public void failed(MigrationMetaInfo metaInfo) throws IOException {
        metaInfo.setStatus(FAILED);
        documentService.update(cartographerConfiguration.getSchemaIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    private JsonNode getJsonNode(MigrationMetaInfo metaInfa) throws IOException {
        return objectMapper.readTree(objectMapper.writeValueAsBytes(metaInfa));
    }
}
