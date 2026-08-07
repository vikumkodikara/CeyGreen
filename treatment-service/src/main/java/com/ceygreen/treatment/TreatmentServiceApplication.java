package com.ceygreen.treatment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 3 microservice: Treatment and Suggestion.
 *
 * <p>Given a disease name (received from the client, never from another service), returns
 * recommended treatments. Publishes severe-tier recommendations to the
 * {@code treatment-events} Kafka topic.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TreatmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreatmentServiceApplication.class, args);
    }
}
