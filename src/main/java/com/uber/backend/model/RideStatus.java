package com.uber.backend.model;

public enum RideStatus {
    REQUESTED, // Rider ne cab dhoondhna shuru kiya
    ACCEPTED,  // Driver mil gaya
    COMPLETED, // Ride khatam
    CANCELLED  // Kisi ne ride cancel kar di
}