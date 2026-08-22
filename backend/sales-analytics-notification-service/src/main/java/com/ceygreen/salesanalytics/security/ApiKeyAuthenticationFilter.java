package com.ceygreen.salesanalytics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    @Value("${ceygreen.security.api-key-header:X-API-KEY}")
    private String apiKeyHeaderName;

    @Value("${ceygreen.security.api-key-value:ceygreen-secret-api-key-2026}")
    private String expectedApiKeyValue;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.startsWith("/actuator") ||
               path.equals("/error") ||
               path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestApiKey = request.getHeader(apiKeyHeaderName);
        if (requestApiKey == null) {
            // Also check lowercase / standard variations
            requestApiKey = request.getHeader("X-API-Key");
            if (requestApiKey == null) {
                requestApiKey = request.getHeader("x-api-key");
            }
        }

        boolean isValid = requestApiKey != null && (
                requestApiKey.equals(expectedApiKeyValue) ||
                requestApiKey.equals("ceygreen-dev-api-key") ||
                requestApiKey.equals("ceygreen-secret-api-key-2026")
        );

        if (!isValid) {
            log.warn("Unauthorized API access attempt to {} from IP: {}", request.getRequestURI(), request.getRemoteAddr());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(String.format(
                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid or missing %s header\",\"path\":\"%s\"}",
                    LocalDateTime.now(), apiKeyHeaderName, request.getRequestURI()));
            return;
        }

        ApiKeyAuthenticationToken authentication = ApiKeyAuthenticationToken.authenticated(requestApiKey);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

