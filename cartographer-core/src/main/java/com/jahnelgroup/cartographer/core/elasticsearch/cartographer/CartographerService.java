package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

import java.io.IOException;
import java.util.List;

public interface CartographerService {

    List<MigrationMetaInfo> fetchMigrations();

    void createIndex() throws IOException, CartographerException;
    boolean indexExists() throws IOException;
    void deleteIndex() throws IOException;

    void pending(MigrationMetaInfo metaInfa) throws IOException, Exception;
    void success(MigrationMetaInfo metaInfa) throws IOException, Exception;
    void failed(MigrationMetaInfo metaInfa) throws IOException, Exception;


}
