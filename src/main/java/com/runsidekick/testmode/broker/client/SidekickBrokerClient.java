package com.runsidekick.testmode.broker.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.runsidekick.testmode.broker.handler.message.MessageHandler;
import com.runsidekick.testmode.util.ExceptionUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author yasin.kalafat
 */
public final class SidekickBrokerClient extends WebSocketListener {

    private final Logger logger = LogManager.getLogger(getClass());

    private static final long READ_TIMEOUT = 30;
    private static final int CLOSE_TIMEOUT_MSEC = 1000;

    private final OkHttpClient client =
            new OkHttpClient.Builder().
                    readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).
                    pingInterval(Duration.ofMinutes(1)).
                    build();
    private final WebSocket webSocket;
    private final ClientCredentials clientCredentials;
    private final CountDownLatch connectedCountDownLatch = new CountDownLatch(1);
    private final CountDownLatch closedCountDownLatch = new CountDownLatch(1);
    private final ConnectionCallback connectionCallback;
    private final Map<String, InFlightRequest> requestMap = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).
                configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true).
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private MessageHandler messageHandler;

    public SidekickBrokerClient(String host, int port, ClientCredentials clientCredentials,
                                MessageHandler messageHandler) {
        this(host, port, clientCredentials, null, messageHandler);
    }

    public SidekickBrokerClient(String host, int port, ClientCredentials clientCredentials,
                                ConnectionCallback connectionCallback, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.clientCredentials = clientCredentials;
        this.connectionCallback = connectionCallback;
        Request request = buildClientRequest(host, port, clientCredentials);
        webSocket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    private static Request buildClientRequest(String host, int port, ClientCredentials clientCredentials) {
        Request.Builder builder = new Request.Builder();
        if (host.startsWith("ws://") || host.startsWith("wss://")) {
            builder.url(host + ":" + port + "/client");
        } else {
            builder.url("wss://" + host + ":" + port + "/client");
        }
        builder.header("x-sidekick-api-key", clientCredentials.getApiKey());
        builder.header("x-sidekick-api-token", clientCredentials.getApiToken());
        return builder.build();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isConnected() {
        return connectedCountDownLatch.getCount() == 0;
    }

    public boolean waitUntilConnected() {
        try {
            connectedCountDownLatch.await();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean waitUntilConnected(long timeout, TimeUnit unit) {
        try {
            return connectedCountDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void close() {
        try {
            webSocket.close(CLOSE_TIMEOUT_MSEC, null);
        } catch (Exception e) {
        }
    }

    public void closeAndWait() {
        try {
            webSocket.close(CLOSE_TIMEOUT_MSEC, null);
            waitUntilClosed();
        } catch (Exception e) {
        }
    }

    public boolean isClosed() {
        return closedCountDownLatch.getCount() == 0;
    }

    public boolean waitUntilClosed() {
        try {
            closedCountDownLatch.await();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean waitUntilClosed(long timeout, TimeUnit unit) {
        try {
            return closedCountDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void destroy() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void send(String message) throws IOException {
        webSocket.send(message);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        logger.debug("OPEN: " + response.message());

        connectedCountDownLatch.countDown();
        if (null != connectionCallback) {
            connectionCallback.onConnectSuccess(this);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        logger.debug("MESSAGE: " + text);
        messageHandler.handleMessage(text);

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(text);
        } catch (Exception e) {
            logger.warn("Unable to parse message: " + text, e);
            return;
        }
        checkAndHandleInFlightRequests(text, jsonNode);
    }

    private void checkAndHandleInFlightRequests(String text, JsonNode jsonNode) {
        if (requestMap.size() > 0) {
            String requestId = extractStringProp(jsonNode, "requestId");
            if (requestId != null) {
                InFlightRequest inFlightRequest = requestMap.get(requestId);
                if (inFlightRequest != null) {
                    requestMap.remove(requestId);
                    if (inFlightRequest.scheduledFuture != null) {
                        inFlightRequest.scheduledFuture.cancel(true);
                    }
                    if (inFlightRequest.completableFuture != null) {
                        if (inFlightRequest.responseClass == null
                                || inFlightRequest.responseClass.equals(String.class)) {
                            inFlightRequest.completableFuture.complete(text);
                        } else {
                            try {
                                Object response = objectMapper.readValue(text, inFlightRequest.responseClass);
                                inFlightRequest.completableFuture.complete(response);
                            } catch (IOException e) {
                                inFlightRequest.completableFuture.completeExceptionally(
                                        new IOException("Unable to deserialize response: " + text, e));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        logger.debug("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        logger.debug("CLOSING: " + code + " " + reason);

        webSocket.close(CLOSE_TIMEOUT_MSEC, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        logger.debug("CLOSED: " + code + " " + reason);

        closedCountDownLatch.countDown();
        if (null != connectionCallback) {
            connectionCallback.onClose(this, null);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        logger.warn("FAILED: " + t.getMessage(), t);

        if (!isConnected()) {
            closedCountDownLatch.countDown();
            connectionCallback.onConnectFailure(this, t);
        } else {
            connectionCallback.onClose(this, t);
        }
    }

    public <R> R requestSync(Object request, Class<R> responseClass) {
        return requestSync(request, responseClass, 5, TimeUnit.SECONDS);
    }

    public <R> R requestSync(Object requestObj, Class<R> responseClass, long timeout, TimeUnit timeUnit) {
        try {
            String request = objectMapper.writeValueAsString(requestObj);
            return doRequest(request, responseClass, timeUnit.toMillis(timeout)).get(timeout, timeUnit);
        } catch (JsonProcessingException e) {
            ExceptionUtil.sneakyThrow(new IOException("Unable to serialize request", e));
            return null;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            ExceptionUtil.sneakyThrow(new IOException("Request failed", e));
            return null;
        }
    }

    private <R> CompletableFuture<R> doRequest(String request, Class<R> responseClass, long timeoutMsec) {
        CompletableFuture<R> completableFuture = new CompletableFuture();
        String requestId = extractRequestId(request, "id");
        if (requestId == null) {
            throw new IllegalArgumentException("Request must contain 'requestId'");
        }
        ScheduledFuture scheduledFuture =
                scheduledExecutorService.schedule(() -> {
                    InFlightRequest inFlightRequest = requestMap.remove(requestId);
                    if (inFlightRequest != null) {
                        if (inFlightRequest.completableFuture != null) {
                            inFlightRequest.completableFuture.completeExceptionally(
                                    new TimeoutException(
                                            String.format("Request with id %s has timed-out", requestId)));
                        }
                    }
                }, timeoutMsec, TimeUnit.MILLISECONDS);
        requestMap.put(requestId, new InFlightRequest(completableFuture, scheduledFuture, responseClass));
        try {
            send(request);
        } catch (Throwable t) {
            requestMap.remove(requestId);
            scheduledFuture.cancel(true);
            ExceptionUtil.sneakyThrow(t);
        }
        return completableFuture;
    }

    private static String extractRequestId(String message, String requestIdField) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            return extractStringProp(jsonNode, requestIdField);
        } catch (Exception e) {
        }
        return null;
    }

    private static String extractStringProp(JsonNode jsonNode, String prop) {
        try {
            if (jsonNode.has(prop)) {
                return jsonNode.get(prop).asText();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static class InFlightRequest {

        private final CompletableFuture completableFuture;
        private final ScheduledFuture scheduledFuture;
        private final Class responseClass;
        private final Map<String, Object> applicationResponseMap;


        private InFlightRequest(CompletableFuture completableFuture,
                                ScheduledFuture scheduledFuture,
                                Class responseClass) {
            this.completableFuture = completableFuture;
            this.scheduledFuture = scheduledFuture;
            this.responseClass = responseClass;
            this.applicationResponseMap = new ConcurrentHashMap<>();
        }
    }

    public interface ConnectionCallback {

        void onConnectSuccess(SidekickBrokerClient client);

        void onConnectFailure(SidekickBrokerClient client, Throwable t);

        void onClose(SidekickBrokerClient client, Throwable t);

    }

}
