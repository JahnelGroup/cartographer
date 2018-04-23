package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.Migration;
import com.jahnelgroup.cartographer.core.migration.MigrationMetaInfo;

public class DefaultSchemaMigrationDocumentIdGenerator implements SchemaMigrationDocumentIdGenerator {

    @Override
    public String generateDocumentId(MigrationMetaInfo metaInfo) {
        return metaInfo.getIndex() + "_" + metaInfo.getVersion();
    }

}
