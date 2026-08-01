package com.uber.backend.controller;

import com.uber.backend.dto.RideRequestDTO;
import com.uber.backend.model.Ride;
import com.uber.backend.service.RideService;
import com.uber.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;
    private final NotificationService notificationService;

    public RideController(RideService rideService, NotificationService notificationService) {
        this.rideService = rideService;
        this.notificationService = notificationService;
    }

    @PostMapping("/request")
    public ResponseEntity<Ride> createRideRequest(@RequestBody RideRequestDTO dto) {
        Ride requestedRide = rideService.createRideRequest(dto);
        return ResponseEntity.ok(requestedRide);
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<String> acceptRide(@PathVariable Long rideId, @RequestParam Long driverId) {
        String result = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{rideId}/stream-location")
    public ResponseEntity<String> streamLocation(
            @PathVariable Long rideId, @RequestParam Long driverId,
            @RequestParam double lat, @RequestParam double lng) {
        rideService.verifyAndStreamLocation(rideId, driverId, lat, lng);
        return ResponseEntity.ok("Location Verified and Streamed Successfully!");
    }

    // 🛑 WAPAS AAYA: Ride Reject karne ke liye
    @PostMapping("/{rideId}/reject")
    public ResponseEntity<String> rejectRide(@PathVariable Long rideId, @RequestParam Long driverId) {
        String result = rideService.rejectRide(rideId, driverId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<String> completeRide(@PathVariable Long rideId) {
        String result = rideService.completeRide(rideId);
        return ResponseEntity.ok(result);
    }
}