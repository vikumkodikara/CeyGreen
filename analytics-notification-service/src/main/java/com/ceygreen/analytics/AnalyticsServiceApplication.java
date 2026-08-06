package com.ceygreen.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 6 microservice: Sales Analytics and Notifications.
 *
 * <p>Consumes events from all six Kafka topics (greenhouse-alerts, diagnosis-events,
 * treatment-events, order-events, stock-events, forum-events), aggregates sales data,
 * and provides analytics endpoints and notification management.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
