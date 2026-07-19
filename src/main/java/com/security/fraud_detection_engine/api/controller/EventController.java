package com.security.fraud_detection_engine.api.controller;

import com.security.fraud_detection_engine.api.dto.request.FraudEventRequest;
import com.security.fraud_detection_engine.api.dto.response.DecisionResponse;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.repository.postgres.TenantRepository;
import com.security.fraud_detection_engine.service.FraudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class EventController {

    private final FraudService fraudService;
    private final TenantRepository tenantRepository;

    public EventController(FraudService fraudService,
                           TenantRepository tenantRepository) {
        this.fraudService = fraudService;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/events")
    public ResponseEntity<DecisionResponse> evaluate(
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody FraudEventRequest request) {

        Tenant tenant = tenantRepository
            .findByApiKeyAndIsActiveTrue(apiKey)
            .orElse(null);

        if (tenant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DecisionResponse response = fraudService.evaluate(tenant, request);
        return ResponseEntity.ok(response);
    }
}
