package com.jahnelgroup.cartographer.core.event;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InfoLogEventListener implements EventListener {

    @Override
    public void event(Event event) {

        switch(event.type()){
            case BEFORE_EACH_MIGRATION:
                log.info("Applying migration mapping {} to index {}.",
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getIndex());
                break;
            case BEFORE_EACH_MIGRATION_VALIDATION:
                log.info("Validating migration mapping {} for index {}.",
                        event.migration().getMetaInfo().getFilename(),
                        event.migration().getMetaInfo().getIndex());
        }

    }

}
