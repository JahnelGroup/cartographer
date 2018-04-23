package com.jahnelgroup.cartographer.core.elasticsearch.snapshot;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import com.jahnelgroup.cartographer.core.http.client.ElasticsearchHttpClient;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService, ConfigurationAware {

    @NonNull
    private ElasticsearchHttpClient elasticsearchHttpClient;

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public void takeSnapshop() {
        throw new UnsupportedOperationException();
    }
}
