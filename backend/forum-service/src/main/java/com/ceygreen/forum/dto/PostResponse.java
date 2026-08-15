package com.ceygreen.forum.dto;

import com.ceygreen.forum.model.Reply;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Post representation returned by the forum API. In list responses {@code replies} is null and only
 * {@code replyCount} is populated, to keep the payload small.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(
        String id,
        String authorId,
        String authorName,
        String title,
        String body,
        List<String> tags,
        String cropType,
        boolean resolved,
        String acceptedReplyId,
        boolean flagged,
        int flagCount,
        boolean aiAnswerAttempted,
        int upvotes,
        int downvotes,
        List<String> upvotedBy,
        List<String> downvotedBy,
        int views,
        List<Reply> replies,
        int replyCount,
        Instant createdAt,
        Instant updatedAt) {
}
