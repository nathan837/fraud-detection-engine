package com.security.fraud_detection_engine.async;

import com.security.fraud_detection_engine.domain.*;
import com.security.fraud_detection_engine.domain.enums.DecisionType;
import com.security.fraud_detection_engine.engine.velocity.VelocityEngine;
import com.security.fraud_detection_engine.repository.postgres.DecisionRepository;
import com.security.fraud_detection_engine.repository.postgres.EventRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncEventPersister {

    private final EventRepository eventRepository;
    private final DecisionRepository decisionRepository;
    private final VelocityEngine velocityEngine;

    public AsyncEventPersister(EventRepository eventRepository,
                                DecisionRepository decisionRepository,
                                VelocityEngine velocityEngine) {
        this.eventRepository = eventRepository;
        this.decisionRepository = decisionRepository;
        this.velocityEngine = velocityEngine;
    }

    @Async
    public void persist(Tenant tenant, User user, FraudEvent event,
                        int score, DecisionType decision,
                        List<String> signals, int responseMs) {

    
        FraudEvent savedEvent = eventRepository.save(event);

    
        Decision d = Decision.builder()
            .event(savedEvent)
            .tenant(tenant)
            .user(user)
            .riskScore(score)
            .decision(decision)
            .triggeredRules(signals)
            .responseMs(responseMs)
            .build();
        decisionRepository.save(d);

        // Update velocity counter redis
        velocityEngine.recordTransaction(user.getId().toString());

        // Update device in Redis
        if (event.getDeviceId() != null) {
            velocityEngine.recordDevice(
                user.getId().toString(),
                event.getDeviceId()
            );
        }

        // Update location in Redis
        if (event.getCountry() != null) {
            velocityEngine.recordLocation(
                user.getId().toString(), 9.03, 38.74
            );
        }
    }
}
