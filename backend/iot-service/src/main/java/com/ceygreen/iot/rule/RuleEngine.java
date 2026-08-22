package com.ceygreen.iot.rule;

import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.ZoneThresholds;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Hourly suggestion engine: reading vs zone limits → action (Kafka only for severe heat).
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
                    "Open roof vents for passive cooling",
                    RuleSeverity.HIGH));
        } else if (temperature > thresholds.getMaxTemperature()) {
            results.add(new RuleResult(
                    "Cool the zone with the misting/water system",
                    RuleSeverity.NORMAL));
        } else if (temperature < thresholds.getMinTemperature()) {
            results.add(new RuleResult(
                    "Close vents; activate heating if available",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateSoilMoisture(double soilMoisture, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (soilMoisture < thresholds.getMinSoilMoisture()) {
            results.add(new RuleResult(
                    "Trigger irrigation for that zone",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateHumidity(double humidity, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (humidity > thresholds.getMaxHumidity()) {
            results.add(new RuleResult(
                    "Open vents to reduce humidity",
                    RuleSeverity.NORMAL));
        }

        return results;
    }

    List<RuleResult> evaluateNpk(double n, double p, double k, ZoneThresholds thresholds) {
        List<RuleResult> results = new ArrayList<>();

        if (n < thresholds.getMinNitrogen()) {
            results.add(new RuleResult(
                    "Apply Nitrogen at the recommended dose",
                    RuleSeverity.NORMAL));
        }
        if (p < thresholds.getMinPhosphorus()) {
            results.add(new RuleResult(
                    "Apply Phosphorus at the recommended dose",
                    RuleSeverity.NORMAL));
        }
        if (k < thresholds.getMinPotassium()) {
            results.add(new RuleResult(
                    "Apply Potassium at the recommended dose",
                    RuleSeverity.NORMAL));
        }

        return results;
    }
}
