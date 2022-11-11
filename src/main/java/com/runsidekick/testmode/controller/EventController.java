package com.runsidekick.testmode.controller;

import com.runsidekick.testmode.broker.model.ReferenceEvent;
import com.runsidekick.testmode.broker.model.event.EventType;
import com.runsidekick.testmode.controller.request.EventRequest;
import com.runsidekick.testmode.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.runsidekick.testmode.broker.model.event.EventType.ERROR_STACK_SNAPSHOT;
import static com.runsidekick.testmode.broker.model.event.EventType.LOGPOINT;
import static com.runsidekick.testmode.broker.model.event.EventType.TRACEPOINT_SNAPSHOT;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author yasin.kalafat
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/tracepoint")
    public ResponseEntity<List<String>> getTracePointSnapshotEvents(@RequestBody EventRequest eventRequest) {
        if (StringUtils.hasText(eventRequest.getProbeName())) {
            return ok(eventService.getEventsByName(TRACEPOINT_SNAPSHOT, eventRequest));
        }
        return ok(eventService.getEvents(TRACEPOINT_SNAPSHOT, eventRequest));
    }

    @PostMapping("/tracepoint/{tag}")
    public ResponseEntity<List<String>> getTracePointSnapshotEventsByTag(
            @RequestBody EventRequest eventRequest, @PathVariable String tag) {
        return ok(eventService.getEventsByTag(TRACEPOINT_SNAPSHOT, eventRequest, tag));
    }

    @PostMapping("/logpoint")
    public ResponseEntity<List<String>> getLogPointEvents(@RequestBody EventRequest eventRequest) {
        return ok(eventService.getEvents(LOGPOINT, eventRequest));
    }

    @PostMapping("/logpoint/{tag}")
    public ResponseEntity<List<String>> getLogPointEventsByTag(
            @RequestBody EventRequest eventRequest, @PathVariable String tag) {
        return ok(eventService.getEventsByTag(LOGPOINT, eventRequest, tag));
    }

    @GetMapping("/errorstack")
    public ResponseEntity<List<String>> getErrorStackSnapshotEvents() {
        return ok(eventService.getEvents(ERROR_STACK_SNAPSHOT));
    }

    @PostMapping("/referenceevent/{probeName}")
    public ResponseEntity<ReferenceEvent> getReferenceEvent(@RequestBody EventRequest eventRequest,
                                                            @PathVariable String probeName) {
        return ok(eventService.getRefEvent(TRACEPOINT_SNAPSHOT, eventRequest, probeName));
    }

    @PostMapping("/flush")
    public ResponseEntity flush(@RequestParam EventType eventType) {
        eventService.flush(eventType);
        return ok().build();
    }

    @PostMapping("/flushAll")
    public ResponseEntity flushAll() {
        eventService.flushAll();
        return ok().build();
    }
}
