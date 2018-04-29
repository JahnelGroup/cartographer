package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public interface CartographerMigrationDocumentIdGenerator {

    String generateDocumentId(MigrationMetaInfo metaInfo);

}
