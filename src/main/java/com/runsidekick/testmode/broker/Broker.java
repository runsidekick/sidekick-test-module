package com.runsidekick.testmode.broker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.runsidekick.testmode.broker.client.ClientCredentials;
import com.runsidekick.testmode.broker.client.SidekickBrokerClient;
import com.runsidekick.testmode.broker.handler.message.MessageHandler;
import com.runsidekick.testmode.broker.model.request.Request;
import com.runsidekick.testmode.broker.model.request.impl.ListLogPointsRequest;
import com.runsidekick.testmode.broker.model.request.impl.ListTracePointsRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yasin.kalafat
 */
@Component
public class Broker {

    private static final Logger logger = LogManager.getLogger(Broker.class);

    private static final long PROBE_FETCH_SCHEDULE_INTERVAL = 30; // 30 seconds

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

    private ScheduledExecutorService scheduler;

    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).
                configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true).
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Broker(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void init() {
        connectToBroker();
        startScheduler();
    }

    public void connectToBroker() {
        if (client == null || client.isClosed()) {
            CompletableFuture connectFuture = new CompletableFuture();
            client = new SidekickBrokerClient(
                    brokerHost,
                    brokerPort,
                    new ClientCredentials(apiKey, apiToken),
                    new CompletableConnectionCallback(connectFuture, null),
                    this.messageHandler);
        }
    }

    public void startScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    fetchTracePoints();
                } catch (Exception e) {
                    logger.info("Fetch tracePoints failed", e);
                }
                try {
                    fetchLogPoints();
                } catch (Exception e) {
                    logger.info("Fetch logPoints failed", e);
                }
            }
        }, 0, PROBE_FETCH_SCHEDULE_INTERVAL, TimeUnit.SECONDS);
    }

    private void fetchTracePoints() {
        if (client == null || !client.isConnected() || client.isClosed()) {
        } else {
            String requestId = UUID.randomUUID().toString();
            ListTracePointsRequest request = new ListTracePointsRequest();
            request.setId(requestId);
            doPublishRequest(request);
        }
    }

    private void fetchLogPoints() {
        if (client == null || !client.isConnected() || client.isClosed()) {
        } else {
            String requestId = UUID.randomUUID().toString();
            ListLogPointsRequest request = new ListLogPointsRequest();
            request.setId(requestId);
            doPublishRequest(request);
        }
    }

    private void doPublishRequest(Request request) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            if (client != null && client.isConnected()) {
                client.send(requestJson);
            }
        } catch (Throwable error) {
            logger.error("Error occurred while publishing request: " + request, error);
        }
    }

    public <R> R requestSync(Object request, Class<R> responseClass) {
        return client.requestSync(request, responseClass);
    }

    private class CompletableConnectionCallback implements SidekickBrokerClient.ConnectionCallback {

        private final CompletableFuture connectFuture;
        private final CompletableFuture closeFuture;

        private CompletableConnectionCallback(CompletableFuture connectFuture, CompletableFuture closeFuture) {
            this.connectFuture = connectFuture;
            this.closeFuture = closeFuture;
        }

        @Override
        public void onConnectSuccess(SidekickBrokerClient client) {
            if (connectFuture != null) {
                connectFuture.complete(null);
            }
        }

        @Override
        public void onConnectFailure(SidekickBrokerClient client, Throwable t) {
            if (connectFuture != null) {
                connectFuture.completeExceptionally(t);
            }
        }

        @Override
        public void onClose(SidekickBrokerClient client, Throwable t) {
            if (client != null) {
                client.destroy();
            }
            if (closeFuture != null) {
                if (t != null) {
                    closeFuture.completeExceptionally(t);
                } else {
                    closeFuture.complete(null);
                }
            }
        }
    }


}
