package com.runsidekick.testmode.service;

import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.controller.request.EventRequest;

import java.util.List;

/**
 * @author yasin.kalafat
 */
public interface EventService {
    List<String> getEvents(EventType eventType, EventRequest eventRequest);

    void flush(EventType eventType);

    void flushAll();
}
