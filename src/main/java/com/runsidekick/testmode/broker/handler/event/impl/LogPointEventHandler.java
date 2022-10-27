package com.runsidekick.testmode.broker.handler.event.impl;

import com.runsidekick.testmode.broker.handler.event.EventContext;
import com.runsidekick.testmode.broker.model.event.EventAck;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.broker.model.event.impl.LogPointEvent;
import com.runsidekick.testmode.store.EventStore;
import org.springframework.stereotype.Component;

/**
 * @author yasin.kalafat
 */
@Component
public class LogPointEventHandler extends BaseEventHandler<LogPointEvent> {
    private static final String EVENT_NAME = "LogPointEvent";

    public LogPointEventHandler(EventStore eventStore) {
        super(EVENT_NAME, LogPointEvent.class, eventStore);
    }

    @Override
    public EventAck handleEvent(LogPointEvent event, EventContext context) {
        eventStore.save(EventType.LOGPOINT, event.getApplicationName(),
                event.getFileName(), event.getLineNo(), context.getRawMessage());
        return null;
    }

}
