package com.jahnelgroup.cartographer.core.event;

import com.jahnelgroup.cartographer.core.config.CartographerConfiguration;
import com.jahnelgroup.cartographer.core.config.ConfigUtils;
import com.jahnelgroup.cartographer.core.config.ConfigurationAware;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService, ConfigurationAware {

    private CartographerConfiguration cartographerConfiguration;
    private List<EventListener> listeners = new ArrayList<>();

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

    @Override
    public void setCartographerConfiguration(CartographerConfiguration cartographerConfiguration) {
        this.cartographerConfiguration = cartographerConfiguration;
        ConfigUtils.injectCartographerConfiguration(cartographerConfiguration, this.listeners);
    }
}

