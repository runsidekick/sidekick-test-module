package com.runsidekick.testmode.broker.handler.event.impl;

import com.runsidekick.testmode.broker.handler.event.EventContext;
import com.runsidekick.testmode.broker.model.event.EventAck;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.broker.model.event.impl.TracePointSnapshotEvent;
import com.runsidekick.testmode.store.EventStore;
import org.springframework.stereotype.Component;

/**
 * @author yasin.kalafat
 */
@Component
public class TracePointSnapshotEventHandler extends BaseEventHandler<TracePointSnapshotEvent> {
    private static final String EVENT_NAME = "TracePointSnapshotEvent";

    public TracePointSnapshotEventHandler(EventStore eventStore) {
        super(EVENT_NAME, TracePointSnapshotEvent.class, eventStore);
    }

    @Override
    public EventAck handleEvent(TracePointSnapshotEvent event, EventContext context) {
        eventStore.save(EventType.TRACEPOINT_SNAPSHOT, event.getApplicationName(),
                event.getFileName(), event.getLineNo(), context.getRawMessage());
        return null;
    }

}
