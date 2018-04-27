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

        BEFORE_SNAPSHOT,
        AFTER_SNAPSHOT,
        AFTER_SNAPSHOT_ERROR,

        BEFORE_EACH_MIGRATION,
        AFTER_EACH_MIGRATION,

        BEFORE_EACH_MIGRATION_VALIDATION,
        AFTER_EACH_MIGRATION_VALIDATION,
        AFTER_EACH_MIGRATION_VALIDATION_ERROR,

        BEFORE_SCHEMA_CREATE,
        AFTER_SCHEMA_CREATE,

        BEFORE_PUT_MAPPING,
        AFTER_PUT_MAPPING,
        AFTER_PUT_MAPPING_ERROR,

        AFTER_MIGRATION,
        AFTER_MIGRATION_ERROR,
        AFTER_EACH_MIGRATION_ERROR

        }

    @NonNull
    private Type type;

    private Migration migration;

    private Exception exception;

}
