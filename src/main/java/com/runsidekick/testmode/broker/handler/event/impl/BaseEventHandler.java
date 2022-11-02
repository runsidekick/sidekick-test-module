package com.runsidekick.testmode.broker.handler.event.impl;

import com.runsidekick.testmode.broker.handler.event.EventHandler;
import com.runsidekick.testmode.broker.model.event.Event;
import com.runsidekick.testmode.store.EventStore;

/**
 * @author yasin.kalafat
 */
public abstract class BaseEventHandler<E extends Event>
        implements EventHandler<E> {

    protected final String eventName;
    protected final Class<E> eventClass;
    protected final EventStore eventStore;

    protected BaseEventHandler(String eventName, Class<E> eventClass, EventStore eventStore) {
        this.eventName = eventName;
        this.eventClass = eventClass;
        this.eventStore = eventStore;
    }

    public String getEventName() {
        return eventName;
    }

    public Class<E> getEventClass() {
        return eventClass;
    }

}
