package com.security.fraud_detection_engine.engine.context;

import com.security.fraud_detection_engine.domain.FraudEvent;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import com.security.fraud_detection_engine.domain.enums.EventType;
import com.security.fraud_detection_engine.engine.velocity.VelocityEngine;
import com.security.fraud_detection_engine.engine.velocity.VelocityWindow;
import com.security.fraud_detection_engine.repository.postgres.EventRepository;
import com.security.fraud_detection_engine.shared.GeoUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ContextBuilder {

    private final VelocityEngine velocityEngine;
    private final EventRepository eventRepository;

    public ContextBuilder(VelocityEngine velocityEngine,
                          EventRepository eventRepository) {
        this.velocityEngine = velocityEngine;
        this.eventRepository = eventRepository;
    }

    public FraudContext build(Tenant tenant, User user, FraudEvent event) {

        String userId = user.getId().toString();

        // VELOCITY
        long txCount60s  = velocityEngine.getTransactionCount(userId, VelocityWindow.SIXTY_SECONDS);
        long txCount1hr  = velocityEngine.getTransactionCount(userId, VelocityWindow.ONE_HOUR);
        long txCount24hr = velocityEngine.getTransactionCount(userId, VelocityWindow.TWENTY_FOUR_HOURS);

        // DEVICE
        boolean newDevice = false;
        if (event.getDeviceId() != null) {
            newDevice = velocityEngine.isNewDevice(userId, event.getDeviceId());
        }

        // LOCATION
        boolean impossibleTravel = false;
        double distanceKm = 0;
        long minutesSinceLastSeen = 0;

        String lastLocation = velocityEngine.getLastLocation(userId);
        if (lastLocation != null && event.getCountry() != null) {
            String[] parts = lastLocation.split(",");
            double lastLat = Double.parseDouble(parts[0]);
            double lastLng = Double.parseDouble(parts[1]);
            long lastTimestamp = Long.parseLong(parts[2]);
            distanceKm = GeoUtil.distanceKm(lastLat, lastLng, 0, 0);
            minutesSinceLastSeen = (System.currentTimeMillis() - lastTimestamp) / 60000;
            impossibleTravel = distanceKm > 500 && minutesSinceLastSeen < 60;
        }

        // BEHAVIOR
        BigDecimal averageAmount = BigDecimal.ZERO;
        boolean newRecipient = false;
        boolean unusualAmount = false;

        if (event.getEventType() == EventType.TRANSACTION) {
            averageAmount = eventRepository.findAverageAmountByUserSince(
                user, LocalDateTime.now().minusDays(30)
            );
            if (averageAmount == null) averageAmount = BigDecimal.ZERO;

            if (event.getRecipientId() != null) {
                newRecipient = !eventRepository
                    .existsByUserAndRecipientId(user, event.getRecipientId());
            }

            if (event.getAmount() != null && averageAmount.compareTo(BigDecimal.ZERO) > 0) {
                unusualAmount = event.getAmount()
                    .compareTo(averageAmount.multiply(BigDecimal.valueOf(3))) > 0;
            }
        }

        return FraudContext.builder()
            .tenant(tenant)
            .user(user)
            .event(event)
            .txCount60s(txCount60s)
            .txCount1hr(txCount1hr)
            .txCount24hr(txCount24hr)
            .newDevice(newDevice)
            .impossibleTravel(impossibleTravel)
            .distanceKm(distanceKm)
            .minutesSinceLastSeen(minutesSinceLastSeen)
            .userAverageAmount(averageAmount)
            .newRecipient(newRecipient)
            .unusualAmount(unusualAmount)
            .build();
    }
}
