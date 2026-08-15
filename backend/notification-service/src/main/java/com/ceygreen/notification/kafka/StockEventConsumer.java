package com.ceygreen.notification.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes stock-events published by Student 4's E-Commerce service. */
@Component
public class StockEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(StockEventConsumer.class);

    @KafkaListener(topics = "stock-events", groupId = "notification-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed stock event: {}", event);
        // TODO: Create notification for farmer about low stock
    }
}
