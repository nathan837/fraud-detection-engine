package com.security.fraud_detection_engine.api.dto.response;

import com.security.fraud_detection_engine.domain.enums.DecisionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DecisionResponse {

    private UUID decisionId;
    private int riskScore;
    private DecisionType decision;
    private List<String> signals;   // triggered rule names
    private long responseMs;        // how long your engine took
}