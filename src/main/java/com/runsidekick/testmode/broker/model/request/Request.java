package com.runsidekick.testmode.broker.model.request;

/**
 * @author yasin.kalafat
 */
public interface Request {

    default String getType() {
        return "Request";
    }

    String getName();

    String getId();

    String getClient();
}