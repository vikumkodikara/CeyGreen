package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String authorId,
        String authorName,
        String category) {
}
