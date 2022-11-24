package com.runsidekick.testmode.store;

import com.runsidekick.testmode.broker.model.event.EventType;

import java.util.List;

/**
 * @author yasin.kalafat
 */
public interface EventStore {

    List<String> get(EventType eventType, String appName);

    List<String> get(EventType eventType, String appName, String fileName, int lineNo);

    void save(EventType eventType, String appName, String fileName, int lineNo, String event);

    void flush(EventType eventType);
}
