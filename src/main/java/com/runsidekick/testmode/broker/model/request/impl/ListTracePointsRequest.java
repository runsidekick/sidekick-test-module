package com.runsidekick.testmode.broker.model.request.impl;

import lombok.Data;

/**
 * @author yasin.kalafat
 */
@Data
public class ListTracePointsRequest extends BaseRequest {

    @Override
    public String toString() {
        return "ListTracePointsRequest{" +
                "id='" + id + '\'' +
                '}';
    }

}