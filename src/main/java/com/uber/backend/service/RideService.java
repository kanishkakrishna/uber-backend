package com.uber.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.backend.dto.RideRequestDTO;
import com.uber.backend.model.*;
import com.uber.backend.repository.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final NotificationService notificationService;
    private final StringRedisTemplate stringRedisTemplate;
    private final LocationService locationService;

    public RideService(RideRepository rideRepository, UserRepository userRepository,
                       DriverRepository driverRepository, KafkaTemplate<String, String> kafkaTemplate,
                       ObjectMapper objectMapper, RedissonClient redissonClient,
                       NotificationService notificationService, StringRedisTemplate stringRedisTemplate,
                       LocationService locationService) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.redissonClient = redissonClient;
        this.notificationService = notificationService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.locationService = locationService;
    }

    public Ride createRideRequest(RideRequestDTO dto) {
        User rider = userRepository.findById(dto.getRiderId())
                .orElseThrow(() -> new RuntimeException("Rider nahi mila!"));

        Ride ride = new Ride();
        ride.setRider(rider);
        ride.setPickupLocation(dto.getPickupLocation());
        ride.setPickupLat(dto.getPickupLatitude());
        ride.setPickupLng(dto.getPickupLongitude());
        ride.setDropoffLocation(dto.getDropoffLocation());
        ride.setFare(250.50);
        ride.setStatus(RideStatus.REQUESTED);

        Ride savedRide = rideRepository.save(ride);

        try {
            kafkaTemplate.send("ride-requests", objectMapper.writeValueAsString(dto));
        } catch (Exception e) {}
        return savedRide;
    }

    public void verifyAndStreamLocation(Long rideId, Long driverId, double lat, double lng) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride nahi mili!"));
        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Unauthorized!");
        }
        notificationService.streamDriverLocation(ride.getRider().getId(), lat, lng);
    }

    public String acceptRide(Long rideId, Long driverId) {
        String lockKey = "ride_lock_" + rideId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean gotLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (gotLock) {
                Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride nahi mili!"));
                Driver acceptedDriver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver nahi mila!"));

                if (RideStatus.ACCEPTED.equals(ride.getStatus())) return "Lock mila par DB update ho chuka tha!";

                ride.setDriver(acceptedDriver);
                ride.setStatus(RideStatus.ACCEPTED);
                rideRepository.save(ride);

                acceptedDriver.setStatus(DriverStatus.ON_TRIP);
                driverRepository.save(acceptedDriver);

                try {
                    stringRedisTemplate.opsForGeo().remove("driver_locations", String.valueOf(driverId));
                } catch (Exception e) {}

                notificationService.notifyRider(ride.getRider().getId(), "Your ride has been accepted by Driver " + driverId);
                return "Badhai ho! Ride " + rideId + " tumhe mil gayi.";
            } else {
                return "Sorry, ye ride kisi aur driver ne pehle hi le li hai.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Server busy hai, please retry.";
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // Record rejected drivers in Redis before cascading the request.
    public String rejectRide(Long rideId, Long driverId) {
        String rejectedKey = "ride_rejected_" + rideId;
        stringRedisTemplate.opsForSet().add(rejectedKey, String.valueOf(driverId));
        stringRedisTemplate.expire(rejectedKey, 1, TimeUnit.HOURS);

        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride nahi mili!"));
        Long nextDriverId = findNextBestDriver(rideId, ride.getPickupLat(), ride.getPickupLng());

        if (nextDriverId != null) {
            notificationService.notifyDriver(nextDriverId, "🔔 Nayi Ride Aayi Hai! Accept karoge? (Ride ID: " + rideId + ")");
            return "Ride rejected. Agle driver (ID: " + nextDriverId + ") ko bhej di gayi hai.";
        } else {
            ride.setStatus(RideStatus.CANCELLED);
            rideRepository.save(ride);
            notificationService.notifyRider(ride.getRider().getId(), "Sorry, aas-paas koi gaadi available nahi hai.");
            return "Ride rejected. Koi aur driver nahi mila.";
        }
    }

    // Find the nearest driver who has not rejected this ride.
    private Long findNextBestDriver(Long rideId, double lat, double lng) {
        List<String> nearestDrivers = locationService.getNearestDrivers(lat, lng, 5.0);
        String rejectedKey = "ride_rejected_" + rideId;

        if (nearestDrivers != null) {
            for (String dIdStr : nearestDrivers) {
                Boolean isRejected = stringRedisTemplate.opsForSet().isMember(rejectedKey, dIdStr);
                if (Boolean.FALSE.equals(isRejected)) {
                    return Long.parseLong(dIdStr);
                }
            }
        }
        return null;
    }

    public String completeRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride nahi mili!"));
        if (RideStatus.COMPLETED.equals(ride.getStatus())) return "Pehle hi complete ho chuki hai!";

        User rider = ride.getRider();
        Driver driver = ride.getDriver();
        Double totalFare = ride.getFare();

        if (rider.getWalletBalance() < totalFare) return "Wallet me paise kam hain!";

        rider.setWalletBalance(rider.getWalletBalance() - totalFare);
        userRepository.save(rider);

        if (driver != null) {
            driver.setEarnings(driver.getEarnings() + totalFare);
            driver.setStatus(DriverStatus.ONLINE);
            driverRepository.save(driver);
        }

        ride.setPaymentStatus("PAID");
        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);

        try {
            locationService.updateDriverLocation(driver.getId(), ride.getPickupLat(), ride.getPickupLng());
        } catch (Exception e) {}

        notificationService.notifyRider(rider.getId(), "Safar khatam! ₹" + totalFare + " kat gaye. Balance: ₹" + rider.getWalletBalance());
        return "Ride Complete & Paid! Naya Wallet Balance: ₹" + rider.getWalletBalance();
    }
}