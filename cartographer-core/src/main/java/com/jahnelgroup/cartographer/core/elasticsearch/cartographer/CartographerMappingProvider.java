package com.jahnelgroup.cartographer.core.elasticsearch.cartographer;

import java.io.IOException;

public interface CartographerMappingProvider {

    String mapping() throws IOException;

}
