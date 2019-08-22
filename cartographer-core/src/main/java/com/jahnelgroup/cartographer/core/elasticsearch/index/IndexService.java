package com.jahnelgroup.cartographer.core.elasticsearch.index;

import com.jahnelgroup.cartographer.core.CartographerException;
import com.jahnelgroup.cartographer.core.migration.Migration;

import java.io.IOException;
import java.util.List;

public interface IndexService {

    IndexDefinition findOne(String index) throws IOException;
    boolean exists(String index) throws IOException;
    List<IndexDefinition> list(String... indexes) throws IOException;
    List<IndexDefinition> list() throws IOException;

    IndexDefinition createIndex(Migration migration) throws IOException, CartographerException;
    IndexDefinition createIndex(String index, String settings, String mappings) throws IOException, CartographerException;

    void openIndex(String index) throws IOException, CartographerException;
    void closeIndex(String index) throws IOException, CartographerException;

    IndexDefinition updateIndex(Migration migration) throws IOException, CartographerException;
    IndexDefinition updateIndex(String index, String settings, String mappings) throws IOException, CartographerException;

    IndexDefinition upsertIndex(Migration migration) throws IOException, CartographerException;
    IndexDefinition upsertIndex(String index, String settings, String mappings) throws CartographerException, IOException;

    IndexDefinition deleteIndex(String index) throws IOException;
}
