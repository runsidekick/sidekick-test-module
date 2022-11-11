package com.runsidekick.testmode.broker.model.response;

/**
 * @author yasin.kalafat
 */
public interface Response {

    default String getType() {
        return "Response";
    }

    String getName();

    String getRequestId();

    boolean isErroneous();

    int getErrorCode();

    String getErrorMessage();

}
