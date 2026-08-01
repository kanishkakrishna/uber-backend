package com.uber.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ye bhi auto increment krega id, id iss table ka primary key hai
    private Long id;// id primary key hai kyunki ye kbhi change nhi hoga

    @Column(nullable = false) // naam null nhi ho skta
    private String name;

    @Column(nullable = false, unique = true) // email bhle user change krwa skta hai apna
    //isiliye ye bhi primary key nhi hai
    private String email;

    @Column(nullable = false, unique = true) // jaisa emial ka bole waisa hi yahan bhi
    private String phone;

    @Column(nullable = false, unique = true)
    private String vehicleNumber; // Gaadi ka number (e.g., DL 1A BC 1234)

    @Column(nullable = false)
    private Double earnings = 0.0; // Shuru me driver ki kamai 0.0 rahegi

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.OFFLINE; // Default status offline rahega

    // Last known location (Asli real-time location toh hum Redis me handle karenge)
    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt = LocalDateTime.now();
}