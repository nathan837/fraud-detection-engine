package com.security.fraud_detection_engine.engine.rules.impl;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.Rule;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Component;

@Component
public class ImpossibleTravelRule implements Rule {

    @Override
    public String getName() {
        return "impossible_travel";
    }

    @Override
    public RuleResult evaluate(FraudContext context) {
        if (context.isImpossibleTravel()) {
            return RuleResult.triggered(
                getName(),
                50,
                String.format("User traveled %.0fkm in %d minutes",
                    context.getDistanceKm(),
                    context.getMinutesSinceLastSeen())
            );
        }
        return RuleResult.notTriggered(getName());
    }
}