package com.runsidekick.testmode.broker.handler.response;

import com.runsidekick.testmode.broker.model.response.Response;

/**
 * @author yasin.kalafat
 */
public interface ResponseHandler<Res extends Response> {

    String getResponseName();

    Class<Res> getResponseClass();

    void handleResponse(Res response);

}