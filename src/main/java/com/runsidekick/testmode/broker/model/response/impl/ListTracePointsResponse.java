package com.runsidekick.testmode.broker.model.response.impl;

import com.runsidekick.testmode.broker.model.probe.TracePoint;
import lombok.Data;

import java.util.List;

@Data
public class ListTracePointsResponse extends BaseResponse {

    private List<TracePoint> tracePoints;

}