package com.ceygreen.iot.rule;

import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.ZoneThresholds;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares sensor readings to zone thresholds and produces suggestions.
 */
@Component
public class RuleEngine {

    public List<RuleResult> evaluate(SensorReading reading, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();
        results.addAll(evaluateTemperature(reading.getTemperature(), thresholds));
        results.addAll(evaluateSoilMoisture(reading.getSoilMoisture(), thresholds));
        results.addAll(evaluateHumidity(reading.getHumidity(), thresholds));
        return results;
    }

    List<RuleResult> evaluateTemperature(double temperature, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (temperature > thresholds.getUrgentMaxTemperature()) {
            results.add(new RuleResult(
                    "URGENT: Open roof — temperature critical",
                    RuleSeverity.HIGH));
        } else if (temperature > thresholds.getMaxTemperature()) {
            results.add(new RuleResult(
                    "Cool the greenhouse",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateSoilMoisture(double soilMoisture, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (soilMoisture < thresholds.getMinSoilMoisture()) {
            results.add(new RuleResult(
                    "Start irrigation",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateHumidity(double humidity, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (humidity > thresholds.getMaxHumidity()) {
            results.add(new RuleResult(
                    "Open vent",
                    RuleSeverity.NORMAL));
        }

        return results;
    }
}
