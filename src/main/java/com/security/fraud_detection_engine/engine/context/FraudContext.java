package com.security.fraud_detection_engine.engine.context;

import com.security.fraud_detection_engine.domain.FraudEvent;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.User;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudContext {

    private Tenant tenant;
    private User user;
    private FraudEvent event;

    private long txCount60s;
    private long txCount1hr;
    private long txCount24hr;

    private boolean newDevice;
    private boolean impossibleTravel;
    private double distanceKm;
    private long minutesSinceLastSeen;

    private BigDecimal userAverageAmount;
    private boolean newRecipient;
    private boolean unusualAmount;
}
