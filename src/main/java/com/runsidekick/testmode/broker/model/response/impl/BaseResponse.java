package com.runsidekick.testmode.broker.model.response.impl;

import com.runsidekick.testmode.broker.model.response.Response;
import lombok.Data;

/**
 * @author yasin.kalafat
 */
@Data
public abstract class BaseResponse implements Response {

    protected String requestId;
    protected boolean erroneous;
    protected int errorCode;
    protected String errorMessage;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

}