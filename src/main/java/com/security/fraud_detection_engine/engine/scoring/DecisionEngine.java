package com.security.fraud_detection_engine.engine.scoring;

import com.security.fraud_detection_engine.domain.enums.DecisionType;
import org.springframework.stereotype.Service;

@Service
public class DecisionEngine {

    private static final int APPROVE_THRESHOLD = 30;
    private static final int BLOCK_THRESHOLD   = 70;

    public DecisionType decide(int riskScore) {
        if (riskScore < APPROVE_THRESHOLD) {
            return DecisionType.APPROVE;
        } else if (riskScore < BLOCK_THRESHOLD) {
            return DecisionType.REVIEW;
        } else {
            return DecisionType.BLOCK;
        }
    }
}