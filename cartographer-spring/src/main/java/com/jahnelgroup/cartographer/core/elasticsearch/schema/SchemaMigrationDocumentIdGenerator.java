package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.Migration;

public interface SchemaMigrationDocumentIdGenerator {

    String generateDocumentId(Migration migration);

}
