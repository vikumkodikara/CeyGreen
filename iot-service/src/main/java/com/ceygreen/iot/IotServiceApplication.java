package com.ceygreen.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 1 microservice: IoT Telemetry and Control.
 *
 * <p>Manages greenhouse blueprints, ingests hourly ESP32 sensor readings into Firebase
 * Realtime Database, evaluates them against configurable zone thresholds, and publishes
 * urgent alerts to the {@code greenhouse-alerts} Kafka topic.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class IotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotServiceApplication.class, args);
    }
}
