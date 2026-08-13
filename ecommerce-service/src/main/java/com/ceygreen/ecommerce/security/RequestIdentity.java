package com.ceygreen.ecommerce.security;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.common.GatewayHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class RequestIdentity {

    private RequestIdentity() {}

    public static UserRole requireRole(HttpServletRequest request, UserRole... allowedRoles) {
        String roleHeader = request.getHeader(GatewayHeaders.USER_ROLE);
        if (roleHeader == null || roleHeader.isBlank()) {
            throw ApiException.forbidden("Missing X-User-Role header");
        }
        UserRole role;
        try {
            role = UserRole.valueOf(roleHeader.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ApiException.forbidden("Invalid X-User-Role header");
        }
        if (Arrays.stream(allowedRoles).anyMatch(allowed -> allowed == role)) {
            return role;
        }
        throw ApiException.forbidden("Operation not allowed for role: " + role);
    }

    public static UUID requireFarmerId(HttpServletRequest request) {
        String header = request.getHeader(GatewayHeaders.FARMER_ID);
        if (header == null || header.isBlank()) {
            throw ApiException.badRequest("Missing X-Farmer-Id header");
        }
        return parseUuid(header, "X-Farmer-Id");
    }

    public static Optional<UserRole> parseRole(HttpServletRequest request) {
        String roleHeader = request.getHeader(GatewayHeaders.USER_ROLE);
        if (roleHeader == null || roleHeader.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UserRole.valueOf(roleHeader.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static Optional<UUID> parseFarmerId(HttpServletRequest request) {
        String header = request.getHeader(GatewayHeaders.FARMER_ID);
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(header.trim()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static Optional<UUID> parseBuyerId(HttpServletRequest request) {
        String header = request.getHeader(GatewayHeaders.BUYER_ID);
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(header.trim()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static UUID requireBuyerId(HttpServletRequest request) {
        String header = request.getHeader(GatewayHeaders.BUYER_ID);
        if (header == null || header.isBlank()) {
            throw ApiException.badRequest("Missing X-Buyer-Id header");
        }
        return parseUuid(header, "X-Buyer-Id");
    }

    private static UUID parseUuid(String value, String headerName) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("Invalid " + headerName + " header");
        }
    }
}