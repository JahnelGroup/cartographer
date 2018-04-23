package com.jahnelgroup.cartographer.core.elasticsearch.schema;

import com.jahnelgroup.cartographer.core.migration.Migration;

import java.io.IOException;
import java.util.List;

public interface SchemaService {

    List<Migration> fetchMigrations();

    void create(Migration migDisk) throws IOException;
    void success(Migration migDisk);
    void failed(Migration migDisk);
}
