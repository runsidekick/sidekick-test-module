package com.runsidekick.testmode.broker.model.event;

/**
 * @author yasin.kalafat
 */
public interface Event {

    default String getType() {
        return "Event";
    }

    String getName();

    String getId();

    boolean isSendAck();

    String getClient();

    long getTime();

    String getHostName();

    String getApplicationName();

    String getApplicationInstanceId();

}
