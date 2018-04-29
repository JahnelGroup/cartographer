package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.JsonNodeDocument;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo.Status.*;

@Data
@AllArgsConstructor
public class CartographerServiceImpl implements CartographerService {

    private CartographerConfiguration cartographerConfiguration;
    private DocumentService documentService;
    private IndexService indexService;
    private CartographerMigrationDocumentIdGenerator schemaMigrationDocumentIdProvider;
    private CartographerMappingProvider cartographerMappingProvider;
    private JsonNodeToMigrationMetaInfoConverter jsonNodeToMigrationMetaInfoConverter;
    private ObjectMapper objectMapper;

    @Override
    public boolean indexExists() throws IOException {
        return indexService.exists(cartographerConfiguration.getCartographerIndex());
    }

    @Override
    public void createIndex() throws IOException, CartographerException {
        indexService.putMapping(cartographerConfiguration.getCartographerIndex(), cartographerMappingProvider.mapping());
    }

    @Override
    public void deleteIndex() throws IOException {
        indexService.deleteIndex(cartographerConfiguration.getCartographerIndex());
    }

    @Override
    public List<MigrationMetaInfo> fetchMigrations() {
        return documentService.findAll(cartographerConfiguration.getCartographerIndex())
            .stream()
                .map(JsonNodeDocument::getJsonNode)
                .map(jsonNodeToMigrationMetaInfoConverter::convert)
                .collect(Collectors.toList())
            ;
    }

    @Override
    public void pending(MigrationMetaInfo metaInfo) throws Exception {
        metaInfo.setStatus(PENDING);
        metaInfo.setDocumentId(schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo));
        documentService.index(cartographerConfiguration.getCartographerIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    @Override
    public void success(MigrationMetaInfo metaInfo) throws Exception {
        metaInfo.setStatus(SUCCESS);
        documentService.update(cartographerConfiguration.getCartographerIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    @Override
    public void failed(MigrationMetaInfo metaInfo) throws Exception {
        metaInfo.setStatus(FAILED);
        documentService.update(cartographerConfiguration.getCartographerIndex(),
                schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                getJsonNode(metaInfo));
    }

    private JsonNode getJsonNode(MigrationMetaInfo metaInfa) throws IOException {
        return objectMapper.readTree(objectMapper.writeValueAsBytes(metaInfa));
    }
}