package com.ceygreen.forum.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Payload published to the {@code forum-events} topic on every new reply, human or AI-generated.
 * Consumed by the Sales Analytics &amp; Notification service; this service is fire-and-forget and
 * does not care whether anything is listening.
 */
public record ForumEvent(
        String eventType,
        String postId,
        String postAuthorId,
        String replyId,
        String replyAuthorId,
        @JsonProperty("isAiGenerated") boolean aiGenerated,
        Instant timestamp) {

    public static final String NEW_REPLY = "NEW_REPLY";
}
