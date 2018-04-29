package com.jahnelgroup.cartographer.core.execute;

import com.jahnelgroup.cartographer.core.event.Event;
import com.jahnelgroup.cartographer.core.event.EventService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecuteRunner {

    private ExecuteContext context;
    private EventService eventService;

    /**
     * Helper that wraps work in a try/catch block to raise corresponding events.
     *
     */
    public void exec() throws Exception {
        try{

            eventService.raise(new Event(Event.Type.valueOf("BEFORE_" + context.eventGroup()))
                    .migration(context.migration()));

            context.work().execute();

            eventService.raise(new Event(Event.Type.valueOf("AFTER_" + context.eventGroup()))
                    .migration(context.migration()));
        }catch(Exception e){
            eventService.raise(new Event(Event.Type.valueOf("AFTER_" + context.eventGroup() + "_ERROR")).exception(e)
                    .migration(context.migration())
                    .exception(e));

            if( context.onFailure() != null ) context.onFailure().execute(e);
            throw e;
        }
    }

}
