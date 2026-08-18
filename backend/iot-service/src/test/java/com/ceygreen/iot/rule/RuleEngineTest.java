package com.ceygreen.iot.rule;

import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.ZoneThresholds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void coolGreenhouseWhenTemperatureAboveMax() {
        SensorReading reading = reading(33, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Cool the greenhouse")));
        assertEquals(RuleSeverity.NORMAL,
                results.stream().filter(r -> r.getMessage().contains("Cool")).findFirst().orElseThrow().getSeverity());
    }

    @Test
    void urgentAlertWhenTemperatureCritical() {
        SensorReading reading = reading(40, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(RuleResult::isUrgent));
        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Open roof")));
    }

    @Test
    void startIrrigationWhenSoilTooDry() {
        SensorReading reading = reading(25, 70, 10, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Start irrigation")));
        assertTrue(results.stream().anyMatch(RuleResult::isUrgent));
    }

    @Test
    void suggestNitrogenWhenNIsLow() {
        SensorReading reading = reading(25, 70, 40, 5, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Nitrogen")));
    }

    @Test
    void startIrrigationWhenSoilBelowIdeal() {
        SensorReading reading = reading(25, 70, 27, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().equals("Start irrigation")));
    }

    @Test
    void warmGreenhouseWhenTemperatureBelowMin() {
        SensorReading reading = reading(23, 70, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Warm the greenhouse")));
    }

    @Test
    void raiseHumidityWhenHumidityBelowMin() {
        SensorReading reading = reading(25, 55, 40, 15, 15, 15);

        List<RuleResult> results = ruleEngine.evaluate(reading, thresholds);

        assertTrue(results.stream().anyMatch(r -> r.getMessage().contains("Raise humidity")));
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
