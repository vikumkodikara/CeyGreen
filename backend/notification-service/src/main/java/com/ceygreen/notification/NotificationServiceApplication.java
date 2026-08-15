package com.ceygreen.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Notification microservice.
 *
 * <p>Consumes the five user-facing Kafka topics (greenhouse-alerts, diagnosis-events,
 * treatment-events, stock-events, forum-events), records notifications in
 * {@code notification_log}, and serves notification history and per-user preferences under
 * {@code /notify}.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
