package com.uber.backend.service;

import org.springframework.data.geo.Circle; // Geospatial search area.
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

    // Redis client for geospatial driver locations.
    private final StringRedisTemplate redisTemplate;

    // Redis key containing the driver geo index.
    private static final String DRIVER_LOCATION_KEY = "driver_locations";

    public LocationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Update the driver's live location in Redis.
    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        // Redis geo points use longitude (X) before latitude (Y).
        Point location = new Point(longitude, latitude);

        // Store the driver in the Redis geo index.
        redisTemplate.opsForGeo().add(DRIVER_LOCATION_KEY, location, String.valueOf(driverId));

        System.out.println("Driver " + driverId + " ki location Redis me update ho gayi!");
    }

    // Find drivers within the requested radius.
    public List<String> getNearestDrivers(double latitude, double longitude, double radiusInKm) {
        Point riderLocation = new Point(longitude, latitude); // Rider's current coordinates.
        Distance searchRadius = new Distance(radiusInKm, Metrics.KILOMETERS); // Search radius in kilometers.

        // Build the geospatial search area.
        Circle searchArea = new Circle(riderLocation, searchRadius);

        // Query drivers within the search area.
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(DRIVER_LOCATION_KEY, searchArea);

        List<String> nearestDrivers = new ArrayList<>();

        // Collect matching driver IDs.
        if (results != null) {
            results.forEach(result -> nearestDrivers.add(result.getContent().getName()));
        }

        return nearestDrivers;
    }
}