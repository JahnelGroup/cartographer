package com.jahnelgroup.cartographer.core.elasticsearch.snapshot;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.http.ElasticsearchHttpClient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private CartographerConfiguration cartographerConfiguration;
    private ElasticsearchHttpClient elasticsearchHttpClient;

    @Override
    public void takeSnapshop() {
        throw new UnsupportedOperationException();
    }
}
