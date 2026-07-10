package com.security.fraud_detection_engine.engine.rules;

import com.security.fraud_detection_engine.engine.context.FraudContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleEngine {

    private final List<Rule> rules;

    // Spring automatically injects ALL classes that implement Rule
    // So when you add a new rule class, it's automatically included
    public RuleEngine(List<Rule> rules) {
        this.rules = rules;
    }

    public List<RuleResult> evaluate(FraudContext context) {
        return rules.stream()
            .map(rule -> rule.evaluate(context))
            .collect(Collectors.toList());
    }

    // Only return rules that actually triggered
    public List<RuleResult> evaluateTriggered(FraudContext context) {
        return rules.stream()
            .map(rule -> rule.evaluate(context))
            .filter(RuleResult::isTriggered)
            .collect(Collectors.toList());
    }
}