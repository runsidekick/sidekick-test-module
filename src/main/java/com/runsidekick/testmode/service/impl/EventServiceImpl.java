package com.runsidekick.testmode.service.impl;

import com.runsidekick.testmode.broker.Broker;
import com.runsidekick.testmode.broker.model.ApplicationFilter;
import com.runsidekick.testmode.broker.model.ReferenceEvent;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.broker.model.probe.BaseProbe;
import com.runsidekick.testmode.broker.model.probe.ProbeType;
import com.runsidekick.testmode.broker.model.request.impl.GetReferenceEventRequest;
import com.runsidekick.testmode.broker.model.response.impl.GetReferenceEventResponse;
import com.runsidekick.testmode.controller.request.EventRequest;
import com.runsidekick.testmode.service.EventService;
import com.runsidekick.testmode.store.EventStore;
import com.runsidekick.testmode.store.ProbeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

/**
 * @author yasin.kalafat
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final Broker broker;

    private final EventStore eventStore;

    private final ProbeStore probeStore;

    @Override
    public List<String> getEvents(EventType eventType) {
        return eventStore.get(eventType);
    }

    @Override
    public List<String> getEvents(EventType eventType, EventRequest eventRequest) {
        return eventStore.get(eventType, eventRequest.getAppName(),
                eventRequest.getFileName(), eventRequest.getLineNo());
    }

    @Override
    public List<String> getEventsByName(EventType eventType, EventRequest eventRequest) {
        BaseProbe probe = null;
        switch (eventType) {
            case TRACEPOINT_SNAPSHOT:
                probe = probeStore.getTracePointByProbeName(eventRequest.getProbeName());
                break;
            case LOGPOINT:
                probe = probeStore.getLogPointByProbeName(eventRequest.getProbeName());
                break;
        }
        if (probe != null) {
            return eventStore.get(eventType, eventRequest.getAppName(), probe.getFileName(), probe.getLineNo());
        }
        return null;
    }

    @Override
    public List<String> getEventsByTag(EventType eventType, EventRequest eventRequest, String tag) {
        List<String> events = new ArrayList<>();
        List<? extends BaseProbe> probes = null;
        switch (eventType) {
            case TRACEPOINT_SNAPSHOT:
                probes = probeStore.getTracePointsByTag(tag);
                break;
            case LOGPOINT:
                probes = probeStore.getLogPointsByTag(tag);
                break;
        }
        if (probes != null && probes.size() > 0) {
            probes.forEach(probe ->
                    events.addAll(eventStore.get(eventType, eventRequest.getAppName(),
                            probe.getFileName(), probe.getLineNo())));
        }
        return events;
    }

    @Override
    public ReferenceEvent getRefEvent(EventType eventType, EventRequest eventRequest, String probeName) {
        BaseProbe probe = null;
        ProbeType probeType = null;
        switch (eventType) {
            case TRACEPOINT_SNAPSHOT:
                probe = probeStore.getTracePointByProbeName(eventRequest.getProbeName());
                probeType = ProbeType.TRACEPOINT;
                break;
            case LOGPOINT:
                probe = probeStore.getLogPointByProbeName(eventRequest.getProbeName());
                probeType = ProbeType.LOGPOINT;
                break;
        }
        if (probe != null) {
            String requestId = UUID.randomUUID().toString();
            GetReferenceEventRequest getReferenceEventRequest = new GetReferenceEventRequest();
            getReferenceEventRequest.setId(requestId);
            getReferenceEventRequest.setProbeId(probe.getId());
            getReferenceEventRequest.setProbeType(probeType);
            getReferenceEventRequest.setApplicationFilter(ApplicationFilter.builder()
                    .name(eventRequest.getAppName())
                    .build());

            try {
                GetReferenceEventResponse response =
                        broker.requestSync(getReferenceEventRequest, GetReferenceEventResponse.class);
                return response.getReferenceEvent();
            } catch (Exception e) {
            }
        }
        return null;
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
