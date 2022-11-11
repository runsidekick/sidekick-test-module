package com.runsidekick.testmode.broker.model.response.impl;

import com.runsidekick.testmode.broker.model.probe.LogPoint;
import lombok.Data;

import java.util.List;

/**
 * @author yasin.kalafat
 */
@Data
public class ListLogPointsResponse extends BaseResponse {

    private List<LogPoint> logPoints;

}