package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import static com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo.Status.FAILED;
import static com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo.Status.PENDING;
import static com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo.Status.SUCCESS;

@Data
@RequiredArgsConstructor
public class SchemaServiceImpl implements SchemaService, ConfigurationAware {

    @NonNull
    private DocumentService documentService;

    @NonNull
    private SchemaMigrationDocumentIdGenerator schemaMigrationDocumentIdProvider;

    @NonNull
    private ObjectMapper objectMapper;

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public List<Migration> fetchMigrations() {
        return null;
    }

    @Override
    public void create(Migration migDisk) throws IOException {
        MigrationMetaInfo metaInfa = migDisk.getMetaInfo();
        metaInfa.setStatus(PENDING);

        documentService.index(metaInfa.getIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(migDisk),
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
