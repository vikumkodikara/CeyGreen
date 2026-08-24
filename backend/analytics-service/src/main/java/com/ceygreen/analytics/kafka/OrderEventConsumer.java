package com.ceygreen.analytics.kafka;

import com.ceygreen.analytics.service.AnalyticsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(AnalyticsService analyticsService, ObjectMapper objectMapper) {
        this.analyticsService = analyticsService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events", groupId = "analytics-group")
    public void consume(String message) {
        log.info("Received order event from Kafka: {}", message);
        try {
            Map<String, Object> payload;
            if (message.trim().startsWith("{")) {
                payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
            } else {
                log.warn("Non-JSON order message ignored: {}", message);
                return;
            }
            analyticsService.processOrderEvent(payload);
        } catch (Exception ex) {
            log.error("Failed to process order event {}: {}", message, ex.getMessage(), ex);
        }
    }
}
