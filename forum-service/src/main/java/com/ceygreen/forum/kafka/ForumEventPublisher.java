package com.ceygreen.forum.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ForumEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ForumEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public ForumEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${ceygreen.kafka.forum-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishNewReply(String postId, String postTitle, String replyAuthorId) {
        kafkaTemplate.send(topic, postId, Map.of(
                "postId", postId, "postTitle", postTitle, "replyAuthorId", replyAuthorId,
                "event", "NEW_REPLY", "timestamp", java.time.Instant.now().toString()));
        log.info("Published forum event: NEW_REPLY on post={}", postId);
    }
}
