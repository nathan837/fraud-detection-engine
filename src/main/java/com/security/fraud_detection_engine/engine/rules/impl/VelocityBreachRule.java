package com.security.fraud_detection_engine.engine.rules.impl;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.Rule;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Component;

@Component
public class VelocityBreachRule implements Rule {

    private static final int MAX_TX_PER_60S = 5;

    @Override
    public String getName() {
        return "velocity_breach";
    }

    @Override
    public RuleResult evaluate(FraudContext context) {
        if (context.getTxCount60s() > MAX_TX_PER_60S) {
            return RuleResult.triggered(
                getName(),
                30,
                String.format("%d transactions in last 60 seconds",
                    context.getTxCount60s())
            );
        }
        return RuleResult.notTriggered(getName());
    }
}