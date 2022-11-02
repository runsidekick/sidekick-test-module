package com.runsidekick.testmode;

import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.store.EventStore;
import com.runsidekick.testmode.store.impl.EventStoreImpl;
import io.thundra.swark.utils.UUIDUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yasin.kalafat
 */
public class EventStoreTests {

    private final String APP_NAME = "Test App";
    private final String FILE_NAME = "Test.java";
    private final int LINE_NO = 28;

    private EventStoreImpl eventStore = new EventStoreImpl();

    private void init() {
        eventStore = new EventStoreImpl();
        eventStore.init();
    }

    @Test
    public void checkSavedEvents() {
        init();
        String event1 = UUIDUtils.generateId();
        String event2 = UUIDUtils.generateId();
        String event3 = UUIDUtils.generateId();

        saveTracePointSnapshotEvent(event1);
        saveTracePointSnapshotEvent(event2);
        saveTracePointSnapshotEvent(event3);

        List<String> events = eventStore.get(EventType.TRACEPOINT_SNAPSHOT, APP_NAME, FILE_NAME, LINE_NO);

        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
        assertTrue(events.contains(event3));

    }

    @Test
    public void flush() {
        init();
        String event1 = UUIDUtils.generateId();
        String event2 = UUIDUtils.generateId();
        String event3 = UUIDUtils.generateId();

        saveTracePointSnapshotEvent(event1);
        saveTracePointSnapshotEvent(event2);
        saveTracePointSnapshotEvent(event3);

        eventStore.flush(EventType.LOGPOINT);

        List<String> events = eventStore.get(EventType.TRACEPOINT_SNAPSHOT, APP_NAME, FILE_NAME, LINE_NO);

        assertThat(events.size()).isEqualTo(3);

        eventStore.flush(EventType.TRACEPOINT_SNAPSHOT);

        events = eventStore.get(EventType.TRACEPOINT_SNAPSHOT, APP_NAME, FILE_NAME, LINE_NO);

        assertNull(events);
    }

    private void saveTracePointSnapshotEvent(String event) {
        eventStore.save(EventType.TRACEPOINT_SNAPSHOT, APP_NAME, FILE_NAME, LINE_NO, event);
    }

    private void saveLogPointSnapshotEvent(String event) {
        eventStore.save(EventType.LOGPOINT, APP_NAME, FILE_NAME, LINE_NO, event);
    }

    private void saveErrorStackSnapshotEvent(String event) {
        eventStore.save(EventType.ERROR_STACK_SNAPSHOT, APP_NAME, FILE_NAME, LINE_NO, event);
    }
}
