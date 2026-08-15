package com.ceygreen.forum.dto;

import jakarta.validation.constraints.Size;

/**
 * Payload for {@code POST /forum/posts/{id}/replies}. One endpoint serves two purposes so no extra
 * routes are needed:
 * <ul>
 *   <li>{@code action} absent — create a new reply ({@code body} required).</li>
 *   <li>{@code action} present — act on the thread: {@code upvote}, {@code acceptAnswer} or
 *       {@code flag}.</li>
 * </ul>
 * Cross-field rules (e.g. "upvote needs a replyId", "create needs a body") depend on which branch
 * is taken, so they are enforced in the service rather than by bean validation here.
 */
public record ReplyActionRequest(
        String action,
        String replyId,
        @Size(max = 10_000, message = "Reply body must be at most 10000 characters") String body) {

    /** True when this request is a thread action rather than a new reply. */
    public boolean isAction() {
        return action != null && !action.isBlank();
    }
}
