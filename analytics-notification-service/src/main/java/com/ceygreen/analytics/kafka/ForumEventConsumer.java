package com.ceygreen.analytics.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes forum-events published by Student 5's Forum service. */
@Component
public class ForumEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(ForumEventConsumer.class);

    @KafkaListener(topics = "forum-events", groupId = "analytics-notification-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed forum event: {}", event);
        // TODO: Notify the original post author about the new reply
    }
}
