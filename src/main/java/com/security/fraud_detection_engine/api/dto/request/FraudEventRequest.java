package com.security.fraud_detection_engine.api.dto.request;

import com.security.fraud_detection_engine.domain.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FraudEventRequest {

    @NotNull
    private EventType eventType;

    @NotBlank
    private String userId;        // Chapa's own user ID

    // Device signals
    private String deviceId;
    private String ipAddress;
    private String country;
    private String city;
    private String userAgent;

    // Transaction fields — only for TRANSACTION events
    private BigDecimal amount;
    private String currency;
    private String recipientId;
}