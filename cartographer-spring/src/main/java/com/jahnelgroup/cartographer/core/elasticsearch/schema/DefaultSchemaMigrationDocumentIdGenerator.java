package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.Migration;

public class DefaultSchemaMigrationDocumentIdGenerator implements SchemaMigrationDocumentIdGenerator {

    @Override
    public String generateDocumentId(Migration migration) {
        return migration.getMetaInfo().getIndex() + "_" + migration.getMetaInfo().getVersion();
    }

}
