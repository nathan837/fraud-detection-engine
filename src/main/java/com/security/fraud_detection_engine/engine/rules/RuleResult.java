package com.security.fraud_detection_engine.engine.rules;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResult {

    private String ruleName;
    private boolean triggered;
    private int score;
    private String reason;

    public static RuleResult notTriggered(String ruleName) {
        return RuleResult.builder()
            .ruleName(ruleName)
            .triggered(false)
            .score(0)
            .build();
    }

    public static RuleResult triggered(String ruleName, int score, String reason) {
        return RuleResult.builder()
            .ruleName(ruleName)
            .triggered(true)
            .score(score)
            .reason(reason)
            .build();
    }
}
