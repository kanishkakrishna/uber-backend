package com.uber.backend.dto;

import lombok.Data;

@Data
public class RideRequestDTO {
    private Long riderId;
    private String pickupLocation;
    private String dropoffLocation;

    // Ye do nayi cheezein add ki hain radar chalane ke liye
    private double pickupLatitude;
    private double pickupLongitude;
}