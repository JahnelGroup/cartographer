package com.jahnelgroup.cartographer.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {

    List<EventListener> listeners = new ArrayList<>();

    public EventServiceImpl(){
        this.listeners.add(new InfoLogEventListener());
        this.listeners.add(new TraceLogEventListener());
    }

    @Override
    public EventService addListener(EventListener eventListener) {
        listeners.add(eventListener);
        return this;
    }

    @Override
    public void raise(Event event) {
        listeners.stream().forEach(listener -> listener.event(event));
    }
}
