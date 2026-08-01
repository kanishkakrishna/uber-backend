package com.uber.backend.controller;

import com.uber.backend.dto.LocationUpdateDTO;
import com.uber.backend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Ye import zaroori hai

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Har 5 second me driver ka app is API par request marega
    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateDTO dto) {
        // Service ko data de diya taaki wo Redis me save kar de
        locationService.updateDriverLocation(dto.getDriverId(), dto.getLatitude(), dto.getLongitude());

        // Success message wapas bhej diya
        return ResponseEntity.ok("Location updated super-fast in Redis!");
    }

    // Rider is API ko hit karega apne aas-paas ke drivers dekhne ke liye
    @GetMapping("/nearest")
    public ResponseEntity<List<String>> getNearestDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5") double distance // Default 5 KM search karega
    ) {
        List<String> drivers = locationService.getNearestDrivers(latitude, longitude, distance);
        return ResponseEntity.ok(drivers);
    }
} // <-- Dekh bhai, main class ka darwaza sabse last me band hua hai!