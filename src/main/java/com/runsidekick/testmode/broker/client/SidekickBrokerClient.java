package com.runsidekick.testmode.broker.client;

import com.runsidekick.testmode.broker.handler.message.MessageHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    public interface ConnectionCallback {

        void onConnectSuccess(SidekickBrokerClient client);

        void onConnectFailure(SidekickBrokerClient client, Throwable t);

        void onClose(SidekickBrokerClient client, Throwable t);

    }

}
