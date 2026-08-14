package com.ceygreen.iot.rule;

/**
 * One finding from the rule engine (message + how serious it is).
 */
public class RuleResult {

    private final String message;
    private final RuleSeverity severity;

    public RuleResult(String message, RuleSeverity severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public RuleSeverity getSeverity() {
        return severity;
    }

    public boolean isUrgent() {
        return severity == RuleSeverity.HIGH || severity == RuleSeverity.CRITICAL;
    }
}
