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
        results.addAll(evaluateNpk(reading.getN(), reading.getP(), reading.getK(), thresholds));
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
        } else if (temperature < thresholds.getMinTemperature()) {
            results.add(new RuleResult(
                    "Warm the greenhouse",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateSoilMoisture(double soilMoisture, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (soilMoisture < thresholds.getUrgentMinSoilMoisture()) {
            results.add(new RuleResult(
                    "Start irrigation — soil critically dry",
                    RuleSeverity.HIGH));
        } else if (soilMoisture < thresholds.getMinSoilMoisture()) {
            results.add(new RuleResult(
                    "Start irrigation",
                    RuleSeverity.NORMAL));
        } else if (soilMoisture > thresholds.getMaxSoilMoisture()) {
            results.add(new RuleResult(
                    "Reduce irrigation — soil too wet",
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
        } else if (humidity < thresholds.getMinHumidity()) {
            results.add(new RuleResult(
                    "Raise humidity",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateNpk(double n, double p, double k, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (n < thresholds.getMinNitrogen()) {
            results.add(new RuleResult(
                    "Apply Nitrogen fertilizer",
                    RuleSeverity.NORMAL));
        }
        if (p < thresholds.getMinPhosphorus()) {
            results.add(new RuleResult(
                    "Apply Phosphorus fertilizer",
                    RuleSeverity.NORMAL));
        }
        if (k < thresholds.getMinPotassium()) {
            results.add(new RuleResult(
                    "Apply Potassium fertilizer",
                    RuleSeverity.NORMAL));
        }

        return results;
    }
}
