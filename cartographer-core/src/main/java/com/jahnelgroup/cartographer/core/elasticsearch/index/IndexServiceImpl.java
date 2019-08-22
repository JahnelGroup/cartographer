package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class IndexServiceImpl implements IndexService {

    private CartographerConfiguration cartographerConfiguration;
    private RestHighLevelClient restClient;
    private ObjectMapper objectMapper;

    @Override
    public IndexDefinition findOne(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        GetIndexResponse response = restClient.indices().get(request, RequestOptions.DEFAULT);

        return new IndexDefinition(index, response);
    }

    @Override
    public boolean exists(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);

        return restClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    @Override
    public List<IndexDefinition> list(String... indexes) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexes);
        GetIndexResponse response = restClient.indices().get(request, RequestOptions.DEFAULT);

        return Arrays.stream(response.getIndices())
            .map(index -> new IndexDefinition(index, response))
            .collect(Collectors.toList());
    }

    @Override
    public List<IndexDefinition> list() throws IOException {
        return list("*");
    }

    @Override
    public IndexDefinition createIndex(Migration migration) throws IOException, CartographerException {
        MigrationContent migrationJSON = migration.toJson(objectMapper);

        return createIndex(migrationJSON.getIndex(), migrationJSON.getSettings(), migrationJSON.getMappings());
    }

    @Override
    public IndexDefinition createIndex(String index, String settings, String mappings) throws IOException, CartographerException {
        CreateIndexRequest request = new CreateIndexRequest(index);

        if (!settings.isEmpty()) {
            request.settings(settings, XContentType.JSON);
        }

        if (!mappings.isEmpty()) {
            request.mapping(mappings, XContentType.JSON);
        }

        CreateIndexResponse response = restClient.indices().create(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new CartographerException("Did not successfully createIndex index=" + index);
        }

        return this.findOne(index);
    }

    @Override
    public void openIndex(String index) throws IOException, CartographerException {
        OpenIndexRequest request = new OpenIndexRequest(index);

        OpenIndexResponse response = restClient.indices().open(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new CartographerException(index, " unable to open the index.");
        }
    }

    @Override
    public void closeIndex(String index) throws IOException, CartographerException {
        CloseIndexRequest request = new CloseIndexRequest(index);

        AcknowledgedResponse response = restClient.indices().close(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new CartographerException(index, " unable to open the index.");
        }
    }

    private void putSettings(String index, String settings) throws CartographerException, IOException {
        closeIndex(index);

        UpdateSettingsRequest request = new UpdateSettingsRequest(index).settings(settings, XContentType.JSON);
        AcknowledgedResponse response = restClient.indices().putSettings(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new CartographerException(index, " unable to put the settings.");
        }

        openIndex(index);
    }

    private void putMappings(String index, String mappings) throws CartographerException, IOException {
        PutMappingRequest request = new PutMappingRequest(index).source(mappings, XContentType.JSON);

        AcknowledgedResponse response = restClient.indices().putMapping(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new CartographerException(index, " unable to put the mapping.");
        }
    }

    @Override
    public IndexDefinition updateIndex(Migration migration) throws IOException, CartographerException {
        MigrationContent migrationJSON = migration.toJson(objectMapper);

        return updateIndex(migrationJSON.getIndex(), migrationJSON.getSettings(), migrationJSON.getMappings());
    }

    @Override
    public IndexDefinition updateIndex(String index, String settings, String mappings) throws IOException, CartographerException {
        if (!settings.isEmpty()) {
            putSettings(index, settings);
        }

        if (!mappings.isEmpty()) {
            putMappings(index, mappings);
        }

        return this.findOne(index);
    }

    @Override
    public IndexDefinition upsertIndex(Migration migration) throws IOException, CartographerException {
        MigrationContent migrationJSON = migration.toJson(objectMapper);

        return upsertIndex(migrationJSON.getIndex(), migrationJSON.getSettings(), migrationJSON.getMappings());
    }

    @Override
    public IndexDefinition upsertIndex(String index, String settings, String mappings) throws CartographerException, IOException {
        if (!exists(index)) {
            return createIndex(index, settings, mappings);
        } else {
            return updateIndex(index, settings, mappings);
        }
    }

    @Override
    public IndexDefinition deleteIndex(String index) throws IOException {
        IndexDefinition definition = findOne(index);

        DeleteIndexRequest request = new DeleteIndexRequest(index);

        AcknowledgedResponse response = restClient.indices().delete(request, RequestOptions.DEFAULT);

        if (!response.isAcknowledged()) {
            throw new IOException("Unable to delete index=" + index);
        }

        return definition;
    }
}
