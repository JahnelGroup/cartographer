package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.elasticsearch.document.DocumentService;
import com.jahnelgroup.cartographer.core.elasticsearch.document.JsonNodeDocument;
import com.jahnelgroup.cartographer.core.elasticsearch.index.IndexService;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;
import com.jahnelgroup.cartographer.core.migration.compare.DefaultMigrationMetaInfoComparator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
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
        String contents = cartographerMappingProvider.mapping();
        JsonNode payload = objectMapper.readTree(contents);
        String settings = payload.has("settings") ? payload.get("settings").toString() : "";
        String mappings = payload.has("mappings") ? payload.get("mappings").toString() : "";

        indexService.upsertIndex(cartographerConfiguration.getCartographerIndex(), settings, mappings);
    }

    @Override
    public void deleteIndex() throws IOException {
        indexService.deleteIndex(cartographerConfiguration.getCartographerIndex());
    }

    @Override
    public SortedSet<MigrationMetaInfo> fetchMigrations(String index) {
        Supplier<TreeSet<MigrationMetaInfo>> supplier = () -> new TreeSet<>(new DefaultMigrationMetaInfoComparator());
        return documentService.findAll(cartographerConfiguration.getCartographerIndex())
            .stream()
                .map(JsonNodeDocument::getJsonNode)
                .map(jsonNodeToMigrationMetaInfoConverter::convert)
                .filter(mmi -> index.equals(mmi.getIndex()))
                .collect(Collectors.toCollection(supplier))
            ;
    }

    @Override
    public MigrationMetaInfo fetchMigration(String index, Integer version) {
        Optional<MigrationMetaInfo> entry = fetchMigrations(index).stream().filter(mmi -> version.equals(mmi.getVersion())).findFirst();
        if( !entry.isPresent() ){
            throw new RuntimeException("Unable to find migration for index="+index+", version="+version);
        }
        return entry.get();
    }

    @Override
    public void pending(MigrationMetaInfo metaInfo, boolean isRepair) throws Exception {
        metaInfo.setStatus(PENDING);
        metaInfo.setDocumentId(schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo));
        if( isRepair ){
            documentService.update(cartographerConfiguration.getCartographerIndex(),
                    schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                    getJsonNode(metaInfo));
        }else{
            documentService.index(cartographerConfiguration.getCartographerIndex(),
                    schemaMigrationDocumentIdProvider.generateDocumentId(metaInfo),
                    getJsonNode(metaInfo));
        }

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

    private JsonNode getJsonNode(MigrationMetaInfo metaInfo) throws IOException {
        return objectMapper.readTree(objectMapper.writeValueAsBytes(metaInfo));
    }
}
