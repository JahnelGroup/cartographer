package com.jahnelgroup.cartographer.core.event;

import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class Event {

    public enum Type {
        BEFORE_MIGRATION,
        AFTER_MIGRATION,
        AFTER_MIGRATION_ERROR,

        BEFORE_CLEAN,
        AFTER_CLEAN,
        AFTER_CLEAN_ERROR,

        BEFORE_INDEX_MIGRATION,
        AFTER_INDEX_MIGRATION,
        AFTER_INDEX_MIGRATION_ERROR,

        BEFORE_SNAPSHOT,
        AFTER_SNAPSHOT,
        AFTER_SNAPSHOT_ERROR,

        BEFORE_EACH_MIGRATION,
        AFTER_EACH_MIGRATION,
        AFTER_EACH_MIGRATION_ERROR,

        BEFORE_EACH_MIGRATION_VALIDATION,
        AFTER_EACH_MIGRATION_VALIDATION,
        AFTER_EACH_MIGRATION_VALIDATION_ERROR,

        BEFORE_CREATE_SCHEMA,
        AFTER_CREATE_SCHEMA,
        AFTER_CREATE_SCHEMA_ERROR,

        BEFORE_UPDATE_SCHEMA,
        AFTER_UPDATE_SCHEMA,
        AFTER_UPDATE_SCHEMA_ERROR,

        BEFORE_PUT_MAPPING,
        AFTER_PUT_MAPPING,
        AFTER_PUT_MAPPING_ERROR,

        }

    @NonNull
    private Type type;

    private Migration migration;

    private Exception exception;

}
