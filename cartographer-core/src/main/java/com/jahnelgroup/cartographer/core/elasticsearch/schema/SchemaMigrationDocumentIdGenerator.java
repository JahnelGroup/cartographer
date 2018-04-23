package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public interface SchemaMigrationDocumentIdGenerator {

    String generateDocumentId(MigrationMetaInfo metaInfo);

}
