package com.runsidekick.testmode.broker.model.request.impl;

import lombok.Data;

/**
 * @author yasin.kalafat
 */
@Data
public class ListLogPointsRequest extends BaseRequest {

    @Override
    public String toString() {
        return "ListLogPointsRequest{" +
                "id='" + id + '\'' +
                '}';
    }

}