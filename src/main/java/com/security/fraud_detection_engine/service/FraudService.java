package com.security.fraud_detection_engine.service;

import com.security.fraud_detection_engine.api.dto.request.FraudEventRequest;
import com.security.fraud_detection_engine.api.dto.response.DecisionResponse;
import com.security.fraud_detection_engine.async.AsyncEventPersister;
import com.security.fraud_detection_engine.domain.*;
import com.security.fraud_detection_engine.domain.enums.DecisionType;
import com.security.fraud_detection_engine.domain.enums.RiskLevel;
import com.security.fraud_detection_engine.engine.context.ContextBuilder;
import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.RuleEngine;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import com.security.fraud_detection_engine.engine.scoring.DecisionEngine;
import com.security.fraud_detection_engine.engine.scoring.RiskScorer;
import com.security.fraud_detection_engine.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudService {

    private final UserRepository userRepository;
    private final ContextBuilder contextBuilder;
    private final RuleEngine ruleEngine;
    private final RiskScorer riskScorer;
    private final DecisionEngine decisionEngine;
    private final AsyncEventPersister asyncEventPersister;

    public FraudService(UserRepository userRepository,
                        ContextBuilder contextBuilder,
                        RuleEngine ruleEngine,
                        RiskScorer riskScorer,
                        DecisionEngine decisionEngine,
                        AsyncEventPersister asyncEventPersister) {
        this.userRepository = userRepository;
        this.contextBuilder = contextBuilder;
        this.ruleEngine = ruleEngine;
        this.riskScorer = riskScorer;
        this.decisionEngine = decisionEngine;
        this.asyncEventPersister = asyncEventPersister;
    }

    public DecisionResponse evaluate(Tenant tenant, FraudEventRequest request) {

        long startTime = System.currentTimeMillis();

        // Step 1 — find or create user
        User user = userRepository
            .findByTenantAndExternalId(tenant, request.getUserId())
            .orElseGet(() -> createUser(tenant, request.getUserId()));

        // Step 2 — build event object
        FraudEvent event = buildEvent(tenant, user, request);

        // Step 3 — build context
        FraudContext context = contextBuilder.build(tenant, user, event);

        // Step 4 — run rules
        List<RuleResult> results = ruleEngine.evaluateTriggered(context);

        // Step 5 — calculate score
        int score = riskScorer.calculateScore(results);

        // Step 6 — make decision
        DecisionType decision = decisionEngine.decide(score);

        // Step 7 — collect signals
        List<String> signals = results.stream()
            .map(RuleResult::getRuleName)
            .collect(Collectors.toList());

        long responseMs = System.currentTimeMillis() - startTime;

        // Step 8 — persist async (called from separate bean so @Async works)
        asyncEventPersister.persist(
            tenant, user, event, score, decision, signals, (int) responseMs
        );

        return DecisionResponse.builder()
            .riskScore(score)
            .decision(decision)
            .signals(signals)
            .responseMs(responseMs)
            .build();
    }

    private User createUser(Tenant tenant, String externalId) {
        User user = User.builder()
            .tenant(tenant)
            .externalId(externalId)
            .riskLevel(RiskLevel.LOW)
            .isFlagged(false)
            .build();
        return userRepository.save(user);
    }

    private FraudEvent buildEvent(Tenant tenant, User user,
                                   FraudEventRequest request) {
        return FraudEvent.builder()
            .tenant(tenant)
            .user(user)
            .eventType(request.getEventType())
            .deviceId(request.getDeviceId())
            .ipAddress(request.getIpAddress())
            .country(request.getCountry())
            .city(request.getCity())
            .userAgent(request.getUserAgent())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .recipientId(request.getRecipientId())
            .build();
    }
}

