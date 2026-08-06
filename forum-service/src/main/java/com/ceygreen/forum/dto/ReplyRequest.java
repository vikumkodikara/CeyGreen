package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyRequest(
        @NotBlank String content,
        @NotBlank String authorId,
        String authorName) {
}
