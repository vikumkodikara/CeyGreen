package com.ceygreen.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Sales Analytics microservice.
 *
 * <p>Consumes {@code order-events} published by the E-Commerce service, aggregates sales data
 * into {@code sales_summary} / {@code order_log}, and serves the revenue summary, trend and
 * leaderboard endpoints under {@code /analytics}.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
