package com.security.fraud_detection_engine.engine.rules.impl;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.Rule;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Component;

@Component
public class NewRecipientRule implements Rule {

    @Override
    public String getName() {
        return "new_recipient";
    }

    @Override
    public RuleResult evaluate(FraudContext context) {
        if (context.isNewRecipient()) {
            return RuleResult.triggered(
                getName(),
                15,
                "Sending to a recipient never used before"
            );
        }
        return RuleResult.notTriggered(getName());
    }
}