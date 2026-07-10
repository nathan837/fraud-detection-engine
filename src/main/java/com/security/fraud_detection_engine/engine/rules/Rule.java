package com.security.fraud_detection_engine.engine.rules;

import com.security.fraud_detection_engine.engine.context.FraudContext;

public interface Rule {

    // Name of the rule — stored in triggered_rules list
    String getName();

    // Does this rule apply to this event?
    // Returns a RuleResult with score and whether it triggered
    RuleResult evaluate(FraudContext context);
}