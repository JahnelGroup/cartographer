package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import java.io.IOException;

public interface SchemaMappingProvider {

    String mapping() throws IOException;

}
