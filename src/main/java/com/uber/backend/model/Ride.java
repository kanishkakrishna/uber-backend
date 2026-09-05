package com.uber.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Database-generated ride identifier.

    // Rider who requested the ride.
    @ManyToOne // Many rides can belong to one rider.
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    // Driver assigned to the ride.
    @ManyToOne // Many rides can be assigned to one driver over time.
    @JoinColumn(name = "driver_id") // Driver is unset until the ride is accepted.
    private Driver driver;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    @Column(nullable = false)
    private Double pickupLat;

    @Column(nullable = false)
    private Double pickupLng;

    @Enumerated(EnumType.STRING)
    private RideStatus status = RideStatus.REQUESTED; // New rides start in REQUESTED state.

    private Double fare; // Calculated ride fare.
    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();  // Ride creation timestamp.
}