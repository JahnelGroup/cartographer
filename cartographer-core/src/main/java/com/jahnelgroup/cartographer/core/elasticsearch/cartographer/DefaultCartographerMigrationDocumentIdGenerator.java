package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public class DefaultCartographerMigrationDocumentIdGenerator implements CartographerMigrationDocumentIdGenerator {

    @Override
    public String generateDocumentId(MigrationMetaInfo metaInfo) {
        return metaInfo.getIndex() + "_" + metaInfo.getVersion();
    }

}
