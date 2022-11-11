package com.runsidekick.testmode.broker.handler.response.impl;

import com.runsidekick.testmode.broker.handler.response.ResponseHandler;
import com.runsidekick.testmode.broker.model.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author yasin.kalafat
 */
public abstract class BaseResponseHandler<Res extends Response>
        implements ResponseHandler<Res> {

    private static final Logger logger = LogManager.getLogger(BaseResponseHandler.class);

    protected final String responseName;
    protected final Class<Res> responseClass;

    public BaseResponseHandler(String responseName, Class<Res> responseClass) {
        this.responseName = responseName;
        this.responseClass = responseClass;
    }

    @Override
    public String getResponseName() {
        return responseName;
    }


    @Override
    public Class<Res> getResponseClass() {
        return responseClass;
    }

}
