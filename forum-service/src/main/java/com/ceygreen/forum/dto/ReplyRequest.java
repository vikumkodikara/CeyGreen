package com.ceygreen.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /forum/posts/{id}/replies}. Author identity comes from the gateway-forwarded
 * headers, not from this body.
 */
public record ReplyRequest(
        @NotBlank @Size(max = 10_000) String body) {
}
