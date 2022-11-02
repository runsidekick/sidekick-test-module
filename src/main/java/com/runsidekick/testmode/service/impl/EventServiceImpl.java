package com.runsidekick.testmode.service.impl;

import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.controller.request.EventRequest;
import com.runsidekick.testmode.service.EventService;
import com.runsidekick.testmode.store.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yasin.kalafat
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStore eventStore;

    @Override
    public List<String> getEvents(EventType eventType, EventRequest eventRequest) {
        return eventStore.get(eventType, eventRequest.getAppName(),
                eventRequest.getFileName(), eventRequest.getLineNo());
    }

    @Override
    public void flush(EventType eventType) {
        eventStore.flush(eventType);
    }

    @Override
    public void flushAll() {
        eventStore.flush(EventType.TRACEPOINT_SNAPSHOT);
        eventStore.flush(EventType.LOGPOINT);
        eventStore.flush(EventType.ERROR_STACK_SNAPSHOT);
    }
}
