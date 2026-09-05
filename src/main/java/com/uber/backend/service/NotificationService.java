package com.uber.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    // Track active SSE connections by driver and rider ID.
    private final ConcurrentHashMap<Long, SseEmitter> driverEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, SseEmitter> riderEmitters = new ConcurrentHashMap<>();

    // ================= DRIVER METHODS =================

    public SseEmitter subscribeDriver(Long driverId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        driverEmitters.put(driverId, emitter);
        System.out.println("Driver " + driverId + " ne SSE connection on kar liya hai!");

        emitter.onCompletion(() -> driverEmitters.remove(driverId));
        emitter.onTimeout(() -> driverEmitters.remove(driverId));
        emitter.onError((e) -> driverEmitters.remove(driverId));

        return emitter;
    }

    public void notifyDriver(Long driverId, String message) {
        SseEmitter emitter = driverEmitters.get(driverId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("RideRequest").data(message));
                System.out.println("Driver " + driverId + " ko notification bhej diya!");
            } catch (IOException e) {
                driverEmitters.remove(driverId);
            }
        } else {
            System.out.println("Driver " + driverId + " abhi offline hai (SSE connected nahi hai).");
        }
    }

    // ================= RIDER METHODS =================

    public SseEmitter subscribeRider(Long riderId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        riderEmitters.put(riderId, emitter);
        System.out.println("Rider " + riderId + " ne SSE connection on kar liya hai!");

        emitter.onCompletion(() -> riderEmitters.remove(riderId));
        emitter.onTimeout(() -> riderEmitters.remove(riderId));
        emitter.onError((e) -> riderEmitters.remove(riderId));

        return emitter;
    }

    public void notifyRider(Long riderId, String message) {
        SseEmitter emitter = riderEmitters.get(riderId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("RideUpdate").data(message));
                System.out.println("Rider " + riderId + " ko notification bhej diya!");
            } catch (IOException e) {
                riderEmitters.remove(riderId);
            }
        } else {
            System.out.println("Rider " + riderId + " abhi offline hai (SSE connected nahi hai).");
        }
    }
    public void streamDriverLocation(Long riderId, double lat, double lng) {
        SseEmitter emitter = riderEmitters.get(riderId);
        if (emitter != null) {
            try {
                // Serialize the live coordinates as a compact JSON payload.
                String locationJson = String.format("{\"lat\": %f, \"lng\": %f}", lat, lng);

                // Use a dedicated event name so the frontend can route location updates.
                emitter.send(SseEmitter.event().name("LocationUpdate").data(locationJson));
                System.out.println("📍 [SSE LIVE] Rider " + riderId + " ko driver ki live location bhej di: " + locationJson);
            } catch (IOException e) {
                riderEmitters.remove(riderId);
            }
        }
    }
}