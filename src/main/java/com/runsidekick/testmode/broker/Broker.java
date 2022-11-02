package com.runsidekick.testmode.broker;

import com.runsidekick.testmode.broker.client.ClientCredentials;
import com.runsidekick.testmode.broker.client.SidekickBrokerClient;
import com.runsidekick.testmode.broker.handler.message.MessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author yasin.kalafat
 */
@Component
public class Broker {

    @Value("${sidekick.broker.host}")
    private String brokerHost;

    @Value("${sidekick.broker.port:443}")
    private int brokerPort;

    @Value("${sidekick.apikey:}")
    private String apiKey;

    @Value("${sidekick.apitoken:}")
    private String apiToken;

    private SidekickBrokerClient client;

    private final MessageHandler messageHandler;

    public Broker(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void init() {
        if (client == null || client.isClosed()) {
            client = new SidekickBrokerClient(
                    brokerHost,
                    brokerPort,
                    new ClientCredentials(apiKey, apiToken),
                    this.messageHandler);
        }
    }


}
