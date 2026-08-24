package com.ceygreen.analytics.kafka;

import com.ceygreen.analytics.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes order-events published by Student 4's E-Commerce service. */
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final AnalyticsService analyticsService;

    public OrderEventConsumer(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(topics = "order-events", groupId = "analytics-group")
    public void consume(Map<String, Object> event) {
        log.info("Received order event from Kafka: {}", event);
        try {
            analyticsService.processOrderEvent(event);
        } catch (Exception ex) {
            log.error("Failed to process order event {}: {}", event, ex.getMessage(), ex);
        }
    }
}
