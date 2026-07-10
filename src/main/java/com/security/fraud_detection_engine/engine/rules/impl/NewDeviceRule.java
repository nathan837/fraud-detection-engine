package com.security.fraud_detection_engine.engine.rules.impl;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import com.security.fraud_detection_engine.engine.rules.Rule;
import com.security.fraud_detection_engine.engine.rules.RuleResult;
import org.springframework.stereotype.Component;

@Component
public class NewDeviceRule implements Rule {

    @Override
    public String getName() {
        return "new_device";
    }

    @Override
    public RuleResult evaluate(FraudContext context) {
        if (context.isNewDevice()) {
            return RuleResult.triggered(
                getName(),
                20,
                "Transaction from a device never seen before for this user"
            );
        }
        return RuleResult.notTriggered(getName());
    }
}