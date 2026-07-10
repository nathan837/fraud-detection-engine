package com.security.fraud_detection_engine.engine.scoring;

import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskScorer {

    public int calculateScore(List<RuleResult> results) {
        return results.stream()
            .filter(RuleResult::isTriggered)
            .mapToInt(RuleResult::getScore)
            .sum();
    }
}
