package com.jahnelgroup.cartographer.core.event;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class Event {

    public static enum Type {
        BEFORE_MIGRATION,

        BEFORE_SNAPSHOT,
        AFTER_SNAPSHOT,
        AFTER_SNAPSHOT_ERROR,

        BEFORE_EACH_MIGRATION,
        AFTER_EACH_MIGRATION,

        BEFORE_SCHEMA_CREATE,
        AFTER_SCHEMA_CREATE,

        BEFORE_PUT_MAPPING,
        AFTER_PUT_MAPPING,
        AFTER_PUT_MAPPING_ERROR,

        AFTER_MIGRATION,
        AFTER_MIGRATION_ERROR
    }

    @NonNull
    private Type type;

    private Exception exception;

}
