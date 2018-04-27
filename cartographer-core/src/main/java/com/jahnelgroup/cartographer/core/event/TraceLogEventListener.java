package com.jahnelgroup.cartographer.core.event;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TraceLogEventListener implements EventListener {

    @Override
    public void event(Event event) {
        if( log.isTraceEnabled() ){
            String contents = "";
            if( event.type() == Event.Type.BEFORE_EACH_MIGRATION ){
                contents = ", contents="+event.migration().getMigrationFile().getContents();
            }

            log.trace(event + contents);
        }
    }

}
