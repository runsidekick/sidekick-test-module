package com.runsidekick.testmode.broker.handler.event.impl;

import com.runsidekick.testmode.broker.handler.event.EventContext;
import com.runsidekick.testmode.broker.model.event.EventAck;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.broker.model.event.impl.ErrorStackSnapshotEvent;
import com.runsidekick.testmode.store.EventStore;
import org.springframework.stereotype.Component;

/**
 * @author yasin.kalafat
 */
@Component
public class ErrorStackSnapshotEventHandler extends BaseEventHandler<ErrorStackSnapshotEvent> {
    private static final String EVENT_NAME = "ErrorStackSnapshotEvent";

    public ErrorStackSnapshotEventHandler(EventStore eventStore) {
        super(EVENT_NAME, ErrorStackSnapshotEvent.class, eventStore);
    }

    @Override
    public EventAck handleEvent(ErrorStackSnapshotEvent event, EventContext context) {
        eventStore.save(EventType.ERROR_STACK_SNAPSHOT, event.getApplicationName(),
                event.getFileName(), event.getLineNo(), context.getRawMessage());
        return null;
    }

}
