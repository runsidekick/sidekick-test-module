package com.runsidekick.testmode.broker.handler.response.impl;

import com.runsidekick.testmode.broker.model.response.impl.ListTracePointsResponse;
import com.runsidekick.testmode.store.ProbeStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author yasin.kalafat
 */
@Component
public class ListTracePointsResponseHandler
        extends BaseResponseHandler<ListTracePointsResponse> {

    private static final Logger logger = LogManager.getLogger(ListTracePointsResponseHandler.class);

    public static final String RESPONSE_NAME = "ListTracePointsResponse";

    private final ProbeStore probeStore;

    public ListTracePointsResponseHandler(ProbeStore probeStore) {
        super(RESPONSE_NAME, ListTracePointsResponse.class);
        this.probeStore = probeStore;
    }

    @Override
    public void handleResponse(ListTracePointsResponse response) {
        probeStore.setTracePoints(response.getTracePoints());
    }

}
