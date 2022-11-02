package com.runsidekick.testmode.broker.handler.event;

import com.runsidekick.testmode.broker.model.event.Event;
import com.runsidekick.testmode.broker.model.event.EventAck;

/**
 * @author yasin.kalafat
 */
public interface EventHandler<E extends Event> {

    String getEventName();

    Class<E> getEventClass();

    EventAck handleEvent(E event, EventContext context);

}