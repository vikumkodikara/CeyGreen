package com.ceygreen.userdiagnosis.users.dto;

import com.ceygreen.userdiagnosis.users.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * An OAuth 2.0 style access token response. Field names follow RFC 6749 section 5.1, so a
 * standard OAuth client can consume it unchanged.
 *
 * <p>The token is not persisted anywhere: the gateway validates it by signature on every
 * request.
 */
public record TokenResponse(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        long expiresIn,

        @JsonProperty("user_id")
        UUID userId,

        @JsonProperty("role")
        Role role) {

    public static TokenResponse bearer(String accessToken, long expiresInSeconds, UUID userId, Role role) {
        return new TokenResponse(accessToken, "Bearer", expiresInSeconds, userId, role);
    }
}
