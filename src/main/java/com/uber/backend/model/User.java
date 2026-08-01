package com.uber.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double walletBalance = 500.0;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    // YEH NAYA ADD KIYA HAI: Password ke bina login kaise hoga!
    @Column(nullable = false)
    private String password;

    // YEH NAYA ADD KIYA HAI: Default role
    private String role = "RIDER";

    private LocalDateTime createdAt = LocalDateTime.now();
}