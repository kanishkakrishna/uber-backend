package com.uber.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database-generated primary key.
    private Long id;// Stable driver identifier.

    @Column(nullable = false) // Driver name is required.
    private String name;

    @Column(nullable = false, unique = true) // Email is required and unique, but remains mutable user data.
    // It is therefore not used as the primary key.
    private String email;

    @Column(nullable = false, unique = true) // Phone number is required and unique.
    private String phone;

    @Column(nullable = false, unique = true)
    private String vehicleNumber; // Unique vehicle registration number (e.g., DL 1A BC 1234).

    @Column(nullable = false)
    private Double earnings = 0.0; // Drivers start with zero earnings.

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.OFFLINE; // Drivers start offline.

    // Last known coordinates; real-time tracking is stored in Redis.
    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt = LocalDateTime.now();
}