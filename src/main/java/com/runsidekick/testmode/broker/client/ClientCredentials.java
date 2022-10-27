package com.runsidekick.testmode.broker.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author yasin.kalafat
 */
@Data
@RequiredArgsConstructor
public class ClientCredentials {

    private final String apiKey;
    private final String apiToken;

}