package com.jahnelgroup.cartographer.core.event;

public interface EventService {

    EventService addListener(EventListener eventListener);
    void raise(Event event);

}
