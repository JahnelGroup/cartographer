package com.jahnelgroup.cartographer.core.event;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class InfoLogEventListener implements EventListener, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;

    @Override
    public void event(Event event) {

        switch(event.type()){
            case BEFORE_MIGRATION:
                log.info("Starting Elasticsearch mapping migrations.");
                break;
            case AFTER_CLEAN:
                log.info("Cleaned cartographer index {} in Elasticsearch.",
                        cartographerConfiguration.getCartographerIndex());
                break;
            case AFTER_CREATE_SCHEMA:
                log.info("Created cartographer index {} in Elasticsearch.",
                        cartographerConfiguration.getCartographerIndex());
                break;
            case AFTER_CREATE_SCHEMA_ERROR:
                log.info("Error: Failed to create cartographer schema {} in Elasticsearch.",
                        cartographerConfiguration.getCartographerIndex());
                break;
            case AFTER_LOAD_MIGRATIONS_FROM_DISK:
                int size = (Integer)event.bag().get("size");
                if( size > 0 ){
                    log.info("Found {} migrations on disk.", size);
                }else{
                    log.info("No migrations were found.");
                }
                break;
            case AFTER_LOAD_MIGRATIONS_FROM_ELASTICSEARCH:
                log.info("Found {} existing migrations in Elasticsearch for index {}.",
                        event.bag().get("size"),
                        event.bag().get("index"));
                break;
            case AFTER_MIGRATION:
                log.info("Success.");
                break;
            case BEFORE_EACH_AUTO_REPAIR:
                log.info("Auto-Repairing index={} file={} version={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion());
                break;
            case AFTER_EACH_AUTO_REPAIR_ERROR:
                log.info("Error: Auto-Repair failed for index={} file={} version={} reason={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion(),
                        event.exception().getMessage());
                break;
            case BEFORE_EACH_MIGRATION:
                log.info("Migrating index={} file={} version={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion());
                break;
            case AFTER_EACH_MIGRATION_ERROR:
                log.info("Error: Migration failed for index={} file={} version={} reason={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion(),
                        event.exception().getMessage());
                break;
            case BEFORE_EACH_MIGRATION_VALIDATION:
                log.info("Validating index={} file={} version={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion());
                break;
            case AFTER_EACH_MIGRATION_VALIDATION_ERROR:
                log.info("Error: Validation failed for index={} file={} version={} reason={}",
                        event.migration().getMetaInfo().getIndex(),
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getVersion(),
                        event.exception().getMessage());
                break;
        }

    }

}
