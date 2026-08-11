package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostRequest(
        @NotBlank String title,
        @NotBlank String body,
        @NotBlank String authorId,
        String authorName,
        List<String> tags,
        String cropType) {
}
