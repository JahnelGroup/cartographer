package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

public interface CartographerService {

    SortedSet<MigrationMetaInfo> fetchMigrations(String index);
    MigrationMetaInfo fetchMigration(String index, Integer version);

    void createIndex() throws IOException, CartographerException;
    boolean indexExists() throws IOException;
    void deleteIndex() throws IOException;

    void pending(MigrationMetaInfo metaInfa, boolean isRepair) throws IOException, Exception;
    void success(MigrationMetaInfo metaInfa) throws IOException, Exception;
    void failed(MigrationMetaInfo metaInfa) throws IOException, Exception;


}
