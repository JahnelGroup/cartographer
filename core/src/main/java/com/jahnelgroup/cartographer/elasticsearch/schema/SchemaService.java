package com.jahnelgroup.cartographer.elasticsearch.schema;

import com.jahnelgroup.cartographer.migration.Migration;

import java.util.List;

public interface SchemaService {

    List<Migration> fetchMigrations();

    void create(Migration migDisk);
    void success(Migration migDisk);
    void failed(Migration migDisk);
}
