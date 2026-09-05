package com.uber.backend.controller;

import com.uber.backend.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Open an SSE connection for driver notifications.
    @GetMapping(value = "/subscribe/{driverId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long driverId) {
        return notificationService.subscribeDriver(driverId);
    }
    // Open an SSE connection for rider notifications.
    @GetMapping(value = "/subscribe-rider/{riderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeRider(@PathVariable Long riderId) {
        return notificationService.subscribeRider(riderId);
    }
}