package com.runsidekick.testmode.store.impl;

import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.store.EventStore;
import com.runsidekick.testmode.util.GitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yasin.kalafat
 */
@Component
@RequiredArgsConstructor
public class EventStoreImpl implements EventStore {

    private final Map<EventType, Map<String, List<String>>> eventMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        eventMap.put(EventType.TRACEPOINT_SNAPSHOT, new ConcurrentHashMap<>());
        eventMap.put(EventType.LOGPOINT, new ConcurrentHashMap<>());
        eventMap.put(EventType.ERROR_STACK_SNAPSHOT, new ConcurrentHashMap<>());
    }

    @Override
    public List<String> get(EventType eventType) {
        List<String> events = new ArrayList<>();
        Map<String, List<String>> map = eventMap.get(eventType);
        map.values().forEach(list -> events.addAll(list));
        return events;
    }

    @Override
    public List<String> get(EventType eventType, String appName, String fileName, int lineNo) {
        String eventKey = getEventKey(appName, fileName, lineNo);
        if (eventMap.get(eventType).containsKey(eventKey)) {
            return eventMap.get(eventType).get(eventKey);
        }
        return null;
    }

    @Override
    public void save(EventType eventType, String appName, String fileName, int lineNo, String event) {
        String eventKey = getEventKey(appName, fileName, lineNo);
        if (!eventMap.get(eventType).containsKey(eventKey)) {
            eventMap.get(eventType).putIfAbsent(eventKey, new ArrayList<>());
        }
        eventMap.get(eventType).get(eventKey).add(event);
    }

    @Override
    public void flush(EventType eventType) {
        eventMap.get(eventType).clear();
    }

    private String getEventKey(String appName, String fileName, int lineNo) {
        return appName + "::" + GitHelper.normalizeFileName(fileName) + "::" + lineNo;
    }
}
