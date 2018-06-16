package com.jahnelgroup.cartographer.core.event;

import com.jahnelgroup.cartographer.core.migration.Migration;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

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

        BEFORE_EACH_AUTO_REPAIR,
        AFTER_EACH_AUTO_REPAIR,
        AFTER_EACH_AUTO_REPAIR_ERROR,

        BEFORE_INDEX_MIGRATION,
        AFTER_INDEX_MIGRATION,
        AFTER_INDEX_MIGRATION_ERROR,

        BEFORE_SNAPSHOT,
        AFTER_SNAPSHOT,
        AFTER_SNAPSHOT_ERROR,

        AFTER_LOAD_MIGRATIONS_FROM_DISK,
        AFTER_LOAD_MIGRATIONS_FROM_ELASTICSEARCH,

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

        BEFORE_PUT_INDEX,
        AFTER_PUT_INDEX,
        AFTER_PUT_INDEX_ERROR,

        BEFORE_PUT_MAPPING,
        AFTER_PUT_MAPPING,
        AFTER_PUT_MAPPING_ERROR,

        }

    @NonNull
    private Type type;

    private Migration migration;

    private Exception exception;

    @Setter(AccessLevel.NONE)
    private Map<Object, Object> bag = new HashMap<>();

    public Event bag(Object key, Object value){
        bag.put(key, value);
        return this;
    }

}
