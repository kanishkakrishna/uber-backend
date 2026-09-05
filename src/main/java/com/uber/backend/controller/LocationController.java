package com.uber.backend.controller;

import com.uber.backend.dto.LocationUpdateDTO;
import com.uber.backend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Used for nearest-driver response payloads.

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Receive periodic driver location updates.
    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateDTO dto) {
        // Persist the latest driver location in Redis.
        locationService.updateDriverLocation(dto.getDriverId(), dto.getLatitude(), dto.getLongitude());

        // Confirm the update to the client.
        return ResponseEntity.ok("Location updated super-fast in Redis!");
    }

    // Find drivers within the requested radius of the rider.
    @GetMapping("/nearest")
    public ResponseEntity<List<String>> getNearestDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5") double distance // Default search radius is 5 km.
    ) {
        List<String> drivers = locationService.getNearestDrivers(latitude, longitude, distance);
        return ResponseEntity.ok(drivers);
    }
} // End of LocationController.