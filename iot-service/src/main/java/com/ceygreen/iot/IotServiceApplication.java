package com.ceygreen.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 1 microservice: IoT Telemetry and Control.
 *
 * <p>Ingests ESP32 sensor readings, stores them in Firebase Realtime Database,
 * evaluates zone thresholds (rule engine), and publishes severe alerts to the
 * {@code greenhouse-alerts} Kafka topic for Student 6.
 *
 * <p>This service never calls other microservices over REST. Outbound integration
 * is Kafka publish only (fire-and-forget).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class IotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotServiceApplication.class, args);
    }
}
