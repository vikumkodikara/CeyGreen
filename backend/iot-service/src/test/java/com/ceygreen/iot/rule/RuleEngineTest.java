package com.ceygreen.iot.rule;

import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.ZoneThresholds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleEngineTest {

    private RuleEngine ruleEngine;
    private ZoneThresholds thresholds;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngine();
        thresholds = ZoneThresholds.defaults();
    }

    @Test
    void mistWhenTemperatureAbove30() {
        SensorReading reading = reading(31, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("misting")));
        assertEquals(RuleSeverity.NORMAL,
                results.stream().filter(r -> r.getMessage().contains("misting")).findFirst().orElseThrow().getSeverity());
    }

    @Test
    void urgentRoofVentsWhenTemperatureAbove38() {
        SensorReading reading = reading(39, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(RuleResult::isUrgent));
        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("roof vents")));
    }

    @Test
    void heatWhenTemperatureBelow15() {
        SensorReading reading = reading(14, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("heating")));
        assertFalse(results.stream().anyMatch(RuleResult::isUrgent));
    }

    @Test
    void irrigateWhenSoilLow() {
        SensorReading reading = reading(25, 70, 19, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("irrigation")));
        assertFalse(results.stream().anyMatch(RuleResult::isUrgent));
    }

    @Test
    void noWetSoilRule() {
        SensorReading reading = reading(25, 70, 80, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.isEmpty());
    }

    @Test
    void openVentsWhenHumidityVeryHigh() {
        SensorReading reading = reading(25, 91, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("humidity")));
    }

    @Test
    void noLowHumidityRule() {
        SensorReading reading = reading(25, 40, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.isEmpty());
    }

    @Test
    void suggestNitrogenWhenNIsLow() {
        SensorReading reading = reading(25, 70, 40, 5, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Nitrogen")));
    }

    private static SensorReading reading(
            double temperature,
            double humidity,
            double soilMoisture,
            double n,
            double p,
            double k) {
        SensorReading reading = new SensorReading();
        reading.setTemperature(temperature);
        reading.setHumidity(humidity);
        reading.setSoilMoisture(soilMoisture);
        reading.setN(n);
        reading.setP(p);
        reading.setK(k);
        return reading;
    }
}
