package com.runsidekick.testmode.broker.handler.event;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yasin.kalafat
 */
@Getter
@Setter
public class EventContext {
    private boolean eventUpdated;
    private String rawMessage;
    private boolean broadcast;
    private EventContextExtension extension;
}