package com.uber.backend.dto;

import lombok.Data;

@Data
public class LocationUpdateDTO {
    private Long driverId;
    private double latitude;
    private double longitude;
}