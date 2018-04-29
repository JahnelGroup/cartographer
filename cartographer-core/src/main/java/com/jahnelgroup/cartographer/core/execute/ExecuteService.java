package com.jahnelgroup.cartographer.core.execute;

import com.jahnelgroup.cartographer.core.event.EventService;

public class ExecuteService {

    public static EventService eventService;

    @FunctionalInterface
    public interface Execute{
        void execute() throws Exception;
    }

    @FunctionalInterface
    public interface FailedExecute{
        void execute(Exception e) throws Exception;
    }

    public static void E(ExecuteContext context) throws Exception {
        new ExecuteRunner(context, eventService).exec();
    }

}
