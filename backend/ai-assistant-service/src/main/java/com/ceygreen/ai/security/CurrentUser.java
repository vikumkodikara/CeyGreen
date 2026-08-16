package com.ceygreen.ai.security;

import com.ceygreen.ai.common.ApiException;

/**
 * The caller, as reconstructed from the identity headers the API Gateway injects. This service never
 * calls User Management to resolve identity.
 *
 * <p>{@code userId} is null when the request carried a valid API key but no user identity, which is
 * why write operations call {@link #requireUserId()} before recording an author.
 */
public record CurrentUser(String userId, String displayName, String role) {

    public boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    /** The caller's id, or 401 if the gateway forwarded no user identity. */
    public String requireUserId() {
        if (userId == null || userId.isBlank()) {
            throw ApiException.unauthorized("An authenticated user identity is required for this operation");
        }
        return userId;
    }

    /** True when this caller may act on a resource owned by {@code ownerId}. */
    public boolean canActOnBehalfOf(String ownerId) {
        return isAdmin() || (userId != null && userId.equals(ownerId));
    }
}
