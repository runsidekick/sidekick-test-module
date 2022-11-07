package com.runsidekick.testmode.controller;

import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.controller.request.EventRequest;
import com.runsidekick.testmode.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.runsidekick.testmode.broker.model.event.EventType.ERROR_STACK_SNAPSHOT;
import static com.runsidekick.testmode.broker.model.event.EventType.LOGPOINT;
import static com.runsidekick.testmode.broker.model.event.EventType.TRACEPOINT_SNAPSHOT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

/**
 * @author yasin.kalafat
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    @Value("${sidekick.rest.apikey}")
    private String apiKey;

    private final static String API_KEY_HEADER = "x-sidekick-apikey";

    private final EventService eventService;

    @PostMapping("/tracepoint")
    public ResponseEntity<List<String>> getTracePointSnapshotEvents(
            @RequestHeader(API_KEY_HEADER) String apiKey, @RequestBody EventRequest eventRequest) {
        if (!apiKey.equals(this.apiKey)) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
        return ok(eventService.getEvents(TRACEPOINT_SNAPSHOT, eventRequest));
    }

    @PostMapping("/logpoint")
    public ResponseEntity<List<String>> getLogPointEvents(
            @RequestHeader(API_KEY_HEADER) String apiKey, @RequestBody EventRequest eventRequest) {
        if (!apiKey.equals(this.apiKey)) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
        return ok(eventService.getEvents(LOGPOINT, eventRequest));
    }

    @PostMapping("/errorstack")
    public ResponseEntity<List<String>> getErrorStackSnapshotEvents(
            @RequestHeader(API_KEY_HEADER) String apiKey) {
        if (!apiKey.equals(this.apiKey)) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
        return ok(eventService.getEvents(ERROR_STACK_SNAPSHOT));
    }

    @PostMapping("/flush")
    public ResponseEntity flush(@RequestHeader(API_KEY_HEADER) String apiKey, @RequestParam EventType eventType) {
        if (!apiKey.equals(this.apiKey)) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
        eventService.flush(eventType);
        return ok().build();
    }

    @PostMapping("/flushAll")
    public ResponseEntity flushAll(@RequestHeader(API_KEY_HEADER) String apiKey) {
        if (!apiKey.equals(this.apiKey)) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
        eventService.flushAll();
        return ok().build();
    }
}
