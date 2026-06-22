package com.security.fraud_detection_engine.engine.velocity;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class VelocityEngine {

    private final RedisTemplate<String, String> redisTemplate;

    // Key patterns — every key includes userId so data never mixes
    private static final String TX_COUNT_KEY    = "velocity:tx:%s:%s";
    private static final String DEVICE_KEY      = "known:devices:%s";
    private static final String LOCATION_KEY    = "last:location:%s";

    public VelocityEngine(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ─── TRANSACTION VELOCITY ────────────────────────────────────

    // How many transactions has this user made in this time window?
    public long getTransactionCount(String userId, VelocityWindow window) {
        String key = String.format(TX_COUNT_KEY, userId, window.getLabel());
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    // Add 1 to the counter — called after every transaction
    // Counter expires automatically after the window duration
    public void incrementTransactionCount(String userId, VelocityWindow window) {
        String key = String.format(TX_COUNT_KEY, userId, window.getLabel());
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofSeconds(window.getSeconds()));
    }

    // Increment all three windows at once
    // Called after every transaction
    public void recordTransaction(String userId) {
        incrementTransactionCount(userId, VelocityWindow.SIXTY_SECONDS);
        incrementTransactionCount(userId, VelocityWindow.ONE_HOUR);
        incrementTransactionCount(userId, VelocityWindow.TWENTY_FOUR_HOURS);
    }

    // ─── DEVICE TRACKING ─────────────────────────────────────────

    // Has this user used this device before?
    // Returns true if device is new (never seen before)
    public boolean isNewDevice(String userId, String deviceId) {
        String key = String.format(DEVICE_KEY, userId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, deviceId);
        return Boolean.FALSE.equals(isMember);
    }

    // Remember this device for this user
    // Called after every login or transaction
    public void recordDevice(String userId, String deviceId) {
        String key = String.format(DEVICE_KEY, userId);
        redisTemplate.opsForSet().add(key, deviceId);
        // Devices remembered for 90 days
        redisTemplate.expire(key, Duration.ofDays(90));
    }

    // ─── LOCATION TRACKING ───────────────────────────────────────

    // Where was this user last seen?
    // Returns "lat,lng,timestamp" or null if first time
    public String getLastLocation(String userId) {
        String key = String.format(LOCATION_KEY, userId);
        return redisTemplate.opsForValue().get(key);
    }

    // Update last known location
    // Called after every login or transaction
    public void recordLocation(String userId, double lat, double lng) {
        String key = String.format(LOCATION_KEY, userId);
        String value = lat + "," + lng + "," + System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(30));
    }
}