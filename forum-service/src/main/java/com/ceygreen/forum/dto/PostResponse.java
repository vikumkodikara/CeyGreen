package com.ceygreen.forum.dto;

import com.ceygreen.forum.model.Reply;
import java.time.Instant;
import java.util.List;

public record PostResponse(
        String id,
        String title,
        String content,
        String authorId,
        String authorName,
        String category,
        List<Reply> replies,
        int replyCount,
        Instant createdAt,
        Instant updatedAt) {
}
