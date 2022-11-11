package com.runsidekick.testmode.broker.handler.response.impl;

import com.runsidekick.testmode.broker.model.response.impl.ListLogPointsResponse;
import com.runsidekick.testmode.store.ProbeStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author yasin.kalafat
 */
@Component
public class ListLogPointsResponseHandler
        extends BaseResponseHandler<ListLogPointsResponse> {

    private static final Logger logger = LogManager.getLogger(ListLogPointsResponseHandler.class);

    public static final String RESPONSE_NAME = "ListLogPointsResponse";

    private final ProbeStore probeStore;

    public ListLogPointsResponseHandler(ProbeStore probeStore) {
        super(RESPONSE_NAME, ListLogPointsResponse.class);
        this.probeStore = probeStore;
    }

    @Override
    public void handleResponse(ListLogPointsResponse response) {
        probeStore.setLogPoints(response.getLogPoints());
    }

}
