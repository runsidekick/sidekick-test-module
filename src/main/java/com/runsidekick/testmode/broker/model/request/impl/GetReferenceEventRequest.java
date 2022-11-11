package com.runsidekick.testmode.broker.model.request.impl;

import com.runsidekick.testmode.broker.model.ApplicationFilter;
import com.runsidekick.testmode.broker.model.probe.ProbeType;
import lombok.Data;

/**
 * @author yasin.kalafat
 */
@Data
public class GetReferenceEventRequest extends BaseRequest {

    private String probeId;
    private ProbeType probeType;
    private ApplicationFilter applicationFilter;

}
