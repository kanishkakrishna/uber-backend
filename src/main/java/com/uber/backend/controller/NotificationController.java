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

    // Driver jab app kholega toh is API ko hit karke connection chalu kar lega
    //upar /api/notifications iska koi mtlb nahi hai jb andar ka kamra koi bhi
    //count krega tb aayega full api to yahan sirf ek api address hai,
    // wo hai /api/notifications/subscribe/{driverId} dekh yahan kb hit krega driver
    //jb wo online hoga tb isko hit krega aur fir sse emitter object bnayega , dekho
    //dhyan se notificationservice class bnaya aur fir jo uska method hai subscribedriver jisme
    // bnta hai emitter object usi me pass kiya driverid
    //Driver ke liye 👇
    @GetMapping(value = "/subscribe/{driverId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long driverId) {
        return notificationService.subscribeDriver(driverId);
    }
    // (Rider ke liye) 👇
    @GetMapping(value = "/subscribe-rider/{riderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeRider(@PathVariable Long riderId) {
        return notificationService.subscribeRider(riderId);
    }
}