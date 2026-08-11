package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyRequest(
        @NotBlank String body,
        @NotBlank String authorId,
        String authorName) {
}
