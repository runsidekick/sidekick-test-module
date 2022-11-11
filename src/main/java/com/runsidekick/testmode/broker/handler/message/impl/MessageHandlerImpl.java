package com.runsidekick.testmode.broker.handler.message.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.runsidekick.testmode.broker.handler.event.EventContext;
import com.runsidekick.testmode.broker.handler.event.EventHandler;
import com.runsidekick.testmode.broker.handler.message.MessageHandler;
import com.runsidekick.testmode.broker.handler.response.ResponseHandler;
import com.runsidekick.testmode.broker.model.event.Event;
import com.runsidekick.testmode.broker.model.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yasin.kalafat
 */
@Component
public class MessageHandlerImpl implements MessageHandler {
    private static final Logger logger = LogManager.getLogger(MessageHandlerImpl.class);

    private static final String MESSAGE_EVENT_TYPE = "Event";
    private static final String MESSAGE_RESPONSE_TYPE = "Response";
    private final Map<String, EventHandler> eventHandlerMap = new HashMap<>();
    private final Map<String, ResponseHandler> responseHandlerMap = new HashMap<>();

    private final ObjectMapper objectMapper =
            new ObjectMapper().
                    setSerializationInclusion(JsonInclude.Include.NON_NULL).
                    configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                    configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public MessageHandlerImpl(List<EventHandler> eventHandlers,
                              List<ResponseHandler> responseHandlers) {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandlerMap.put(eventHandler.getEventName(), eventHandler);
        }
        for (ResponseHandler responseHandler : responseHandlers) {
            responseHandlerMap.put(responseHandler.getResponseName(), responseHandler);
        }
    }

    @Override
    public void handleMessage(String message) {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(message);
            String type = extractStringProp(jsonNode, "type");
            String name = extractStringProp(jsonNode, "name");
            if (MESSAGE_EVENT_TYPE.equalsIgnoreCase(type)) {
                handleWithEventHandler(eventHandlerMap.get(name), message, new EventContext());
            } else if (MESSAGE_RESPONSE_TYPE.equals(type)) {
                ResponseHandler responseHandler = responseHandlerMap.get(name);
                if (responseHandler != null) {
                    Response response = (Response) objectMapper.readValue(message, responseHandler.getResponseClass());
                    try {
                        responseHandler.handleResponse(response);
                    } catch (Throwable t) {
                        logger.error("Error occurred while handling response for message with name {}: {}", name, message);
                    }
                } else {
                    logger.warn("No response handler could be found for message with name {}: {}", name, message);
                }

            }
        } catch (Exception e) {
            logger.warn("Unable to parse message: " + message, e);
        }
    }


    private void handleWithEventHandler(EventHandler eventHandler, String messageRaw, EventContext context)
            throws JsonProcessingException {
        if (eventHandler != null) {
            Event event = (Event) objectMapper.readValue(messageRaw, eventHandler.getEventClass());
            context.setRawMessage(messageRaw);
            eventHandler.handleEvent(event, context);
        }
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

    private static List<String> extractArrayProp(JsonNode jsonNode, String prop) {
        try {
            if (jsonNode.has(prop) && jsonNode.get(prop).isArray()) {
                JsonNode arrNode = jsonNode.get(prop);
                List<String> arr = new ArrayList<>();
                for (final JsonNode objNode : arrNode) {
                    arr.add(objNode.asText());
                }
                return arr;
            }
        } catch (Exception e) {
        }
        return null;
    }

}
