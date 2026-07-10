package com.security.fraud_detection_engine.engine.rules.impl;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.Rule;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LargeAmountRule implements Rule {

    private static final BigDecimal THRESHOLD = new BigDecimal("50000");

    @Override
    public String getName() {
        return "large_amount";
    }

    @Override
    public RuleResult evaluate(FraudContext context) {
        if (context.getEvent().getAmount() != null &&
            context.getEvent().getAmount().compareTo(THRESHOLD) > 0) {
            return RuleResult.triggered(
                getName(),
                25,
                String.format("Amount %.2f ETB exceeds threshold of 50,000 ETB",
                    context.getEvent().getAmount())
            );
        }
        return RuleResult.notTriggered(getName());
    }
}