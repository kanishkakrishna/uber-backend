package com.uber.backend.dto;

import lombok.Data;

@Data
public class DriverRegistrationDTO {
    private String name;
    private String email;
    private String phone;
    private String vehicleNumber;
}