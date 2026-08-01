package com.uber.backend.model;

public enum DriverStatus {
    ONLINE,    // Driver free hai aur ride le sakta hai
    OFFLINE,   // Driver duty par nahi hai
    ON_TRIP    // Driver already kisi rider ke sath ride par hai
}