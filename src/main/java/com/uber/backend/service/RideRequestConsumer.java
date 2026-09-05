package com.uber.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.backend.dto.RideRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideRequestConsumer {

    private final LocationService locationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public RideRequestConsumer(LocationService locationService, NotificationService notificationService, ObjectMapper objectMapper) {
        this.locationService = locationService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "ride-requests", groupId = "uber-ride-group")
    public void consumeRideRequest(String rideMessage) {
        System.out.println("\n📬 [KAFKA CONSUMER] Nayi request queue se uthai: " + rideMessage);

        try {
            RideRequestDTO dto = objectMapper.readValue(rideMessage, RideRequestDTO.class);
            List<String> matchingDrivers = locationService.getNearestDrivers(
                    dto.getPickupLatitude(), dto.getPickupLongitude(), 5.0
            );

            // Notify only the highest-priority driver in the cascade.
            if (matchingDrivers != null && !matchingDrivers.isEmpty()) {
                String bestDriverStr = matchingDrivers.get(0);
                Long bestDriverId = Long.parseLong(bestDriverStr);

                System.out.println("[KAFKA WORKER] Sabse best driver mila ID: " + bestDriverId);
                notificationService.notifyDriver(bestDriverId, "🔔 [KAFKA] Nayi Ride: " + dto.getPickupLocation() + " ➡️ " + dto.getDropoffLocation());
            } else {
                System.out.println("[KAFKA WORKER] Aas-paas koi driver nahi mila re baba.");
            }
        } catch (Exception e) {
            System.out.println("Consumer me error: " + e.getMessage());
        }
    }
}