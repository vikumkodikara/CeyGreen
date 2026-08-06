package com.ceygreen.iot.rule;

/**
 * How serious a rule-engine finding is.
 * HIGH / CRITICAL will later publish to Kafka for Student 6 notifications.
 */
public enum RuleSeverity {
    NORMAL,
    HIGH,
    CRITICAL
}
