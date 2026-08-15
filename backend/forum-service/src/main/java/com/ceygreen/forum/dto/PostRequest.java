package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Body of {@code POST /forum/posts}. Author identity is deliberately absent: it comes from the
 * identity headers the gateway forwards, never from the request body, so a client cannot post as
 * somebody else.
 */
public record PostRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 10_000) String body,
        List<@Size(max = 40) String> tags,
        @Size(max = 60) String cropType) {
}
