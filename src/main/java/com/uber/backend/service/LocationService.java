package com.uber.backend.service;

import org.springframework.data.geo.Circle; // Ye naya import add hua hai
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    // Spring Boot ka special tool jo Redis se direct baat karega
    private final StringRedisTemplate redisTemplate;

    // Redis me humare Godown (map) ka naam
    private static final String DRIVER_LOCATION_KEY = "driver_locations";

    public LocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Driver ki live location RAM me update karna
    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        // Dhyan dena: Redis hamesha pehle Longitude (X) leta hai, fir Latitude (Y)
        Point location = new Point(longitude, latitude);

        // Redis me command jaayegi: GEOADD driver_locations longitude latitude driverId
        redisTemplate.opsForGeo().add(DRIVER_LOCATION_KEY, location, String.valueOf(driverId));

        System.out.println("Driver " + driverId + " ki location Redis me update ho gayi!");
    }

    // Naya Method: Ek specific location ke X kilometer radius me drivers dhoondhna
    public List<String> getNearestDrivers(double latitude, double longitude, double radiusInKm) {
        Point riderLocation = new Point(longitude, latitude); // Rider kahan hai?
        Distance searchRadius = new Distance(radiusInKm, Metrics.KILOMETERS); // Kitne KM ka chakkar banana hai?

        // FIX: Point aur Distance ko mila kar ek Circle bana diya
        Circle searchArea = new Circle(riderLocation, searchRadius);

        // Redis Radar Command (GEORADIUS) ab Circle lega
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(DRIVER_LOCATION_KEY, searchArea);

        List<String> nearestDrivers = new ArrayList<>();

        // Agar aas-paas drivers mile, toh unki IDs list me daal do
        if (results != null) {
            results.forEach(result -> nearestDrivers.add(result.getContent().getName()));
        }

        return nearestDrivers;
    }
}