package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

import java.io.IOException;
import java.util.List;

public interface SchemaService {

    List<MigrationMetaInfo> fetchMigrations();

    void create(MigrationMetaInfo metaInfa) throws IOException;
    void success(MigrationMetaInfo metaInfa);
    void failed(MigrationMetaInfo metaInfa);
}
