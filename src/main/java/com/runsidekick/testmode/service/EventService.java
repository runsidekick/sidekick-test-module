package com.runsidekick.testmode.service;

import com.runsidekick.testmode.broker.model.ReferenceEvent;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.controller.request.EventRequest;

import java.util.List;

/**
 * @author yasin.kalafat
 */
public interface EventService {

    List<String> getEvents(EventType eventType);

    List<String> getEvents(EventType eventType, EventRequest eventRequest);

    List<String> getEventsByName(EventType eventType, EventRequest eventRequest);

    List<String> getEventsByTag(EventType eventType, EventRequest eventRequest, String tag);

    ReferenceEvent getRefEvent(EventType eventType, EventRequest eventRequest, String probeName);

    void flush(EventType eventType);

    void flushAll();
}
