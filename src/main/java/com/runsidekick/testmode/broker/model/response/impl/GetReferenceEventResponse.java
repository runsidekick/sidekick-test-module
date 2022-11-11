package com.runsidekick.testmode.broker.model.response.impl;

import com.runsidekick.testmode.broker.model.ReferenceEvent;
import lombok.Data;

/**
 * @author yasin.kalafat
 */
@Data
public class GetReferenceEventResponse extends BaseResponse {

    private ReferenceEvent referenceEvent;

}