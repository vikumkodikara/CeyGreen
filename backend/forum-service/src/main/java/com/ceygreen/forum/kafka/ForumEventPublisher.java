package com.ceygreen.forum.kafka;

import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Publishes {@link ForumEvent}s to the {@code forum-events} topic. This service is a producer only
 * and is fire-and-forget: it does not care whether any downstream consumer is listening.
 */
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

    /** Emit a NEW_REPLY event for {@code reply} on {@code post}, keyed by post id for ordering. */
    public void publishNewReply(Post post, Reply reply) {
        ForumEvent event = new ForumEvent(
                ForumEvent.NEW_REPLY,
                post.getId(),
                post.getAuthorId(),
                reply.getId(),
                reply.getAuthorId(),
                reply.isAiGenerated(),
                Instant.now());
        try {
            kafkaTemplate.send(topic, post.getId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish forum event for post={}: {}", post.getId(), ex.getMessage());
                        } else {
                            log.info("Published forum event: NEW_REPLY on post={} reply={} ai={}",
                                    post.getId(), reply.getId(), reply.isAiGenerated());
                        }
                    });
        } catch (Exception ex) {
            log.warn("Failed to publish forum event for post={}: {}", post.getId(), ex.getMessage());
        }
    }
}
