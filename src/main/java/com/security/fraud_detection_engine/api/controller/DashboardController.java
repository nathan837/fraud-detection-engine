package com.security.fraud_detection_engine.api.controller;

import com.security.fraud_detection_engine.domain.Decision;
import com.security.fraud_detection_engine.domain.Tenant;
import com.security.fraud_detection_engine.domain.enums.DecisionType;
import com.security.fraud_detection_engine.repository.postgres.DecisionRepository;
import com.security.fraud_detection_engine.repository.postgres.TenantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final DecisionRepository decisionRepository;
    private final TenantRepository tenantRepository;

    public DashboardController(DecisionRepository decisionRepository,
                               TenantRepository tenantRepository) {
        this.decisionRepository = decisionRepository;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(
            @RequestHeader("X-API-Key") String apiKey) {

        Tenant tenant = tenantRepository
            .findByApiKeyAndIsActiveTrue(apiKey)
            .orElse(null);

        if (tenant == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDateTime since = LocalDateTime.now().minusHours(24);

        long approved = decisionRepository.countByTenantAndDecisionSince(
            tenant, DecisionType.APPROVE, since);
        long reviewed = decisionRepository.countByTenantAndDecisionSince(
            tenant, DecisionType.REVIEW, since);
        long blocked = decisionRepository.countByTenantAndDecisionSince(
            tenant, DecisionType.BLOCK, since);

        long total = approved + reviewed + blocked;

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("approved", approved);
        metrics.put("reviewed", reviewed);
        metrics.put("blocked", blocked);
        metrics.put("total", total);
        metrics.put("blockRate", total > 0
            ? Math.round((double) blocked / total * 100) : 0);

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/decisions")
    public ResponseEntity<List<Decision>> getDecisions(
            @RequestHeader("X-API-Key") String apiKey) {

        Tenant tenant = tenantRepository
            .findByApiKeyAndIsActiveTrue(apiKey)
            .orElse(null);

        if (tenant == null) {
            return ResponseEntity.status(401).build();
        }

        List<Decision> decisions = decisionRepository
            .findByTenantOrderByCreatedAtDesc(tenant);

        return ResponseEntity.ok(
            decisions.stream().limit(20).toList()
        );
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Decision>> getAlerts(
            @RequestHeader("X-API-Key") String apiKey) {

        Tenant tenant = tenantRepository
            .findByApiKeyAndIsActiveTrue(apiKey)
            .orElse(null);

        if (tenant == null) {
            return ResponseEntity.status(401).build();
        }

        List<Decision> alerts = decisionRepository
            .findByTenantAndDecisionAndReviewedByIsNull(
                tenant, DecisionType.BLOCK);

        return ResponseEntity.ok(alerts);
    }
}
