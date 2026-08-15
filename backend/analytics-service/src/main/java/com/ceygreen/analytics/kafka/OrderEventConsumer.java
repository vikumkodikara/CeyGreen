package com.ceygreen.analytics.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes order-events published by Student 4's E-Commerce service. */
@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @KafkaListener(topics = "order-events", groupId = "analytics-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed order event: {}", event);
        // TODO: Update sales_summary and order_log tables
    }
}
