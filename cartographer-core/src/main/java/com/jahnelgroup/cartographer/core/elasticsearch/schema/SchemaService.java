package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

import java.io.IOException;
import java.util.List;

public interface SchemaService {

    List<MigrationMetaInfo> fetchMigrations();

    void createSchemaIndex() throws IOException, CartographerException;
    boolean exists() throws IOException;

    void index(MigrationMetaInfo metaInfa) throws IOException, Exception;
    void success(MigrationMetaInfo metaInfa) throws IOException, Exception;
    void failed(MigrationMetaInfo metaInfa) throws IOException, Exception;
}
