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
    private Long id; // yahan bhi ye har rides ka id hai auto increment , and it is primary key

    // Foreign Key 1: Ye ride kis rider (User) ne book ki hai?
    @ManyToOne // bahut saara rides ko ek User book kr skta hai, MANY- RIDES , ONE-USER
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    // Foreign Key 2: Ye ride kis Driver ko assign hui hai?
    @ManyToOne // yahan bhi bahut saara rides ek driver ke naam ho skta hai alaga alag time pe
    @JoinColumn(name = "driver_id") // Ye nullable (khali) ho sakta hai shuru me
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
    private RideStatus status = RideStatus.REQUESTED; // Default status

    private Double fare; // Ride ka paisa  // column bnega
    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();  // yahan bhi column bnega
}