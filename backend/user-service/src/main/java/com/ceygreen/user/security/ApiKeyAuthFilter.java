package com.ceygreen.user.security;

import com.ceygreen.user.common.GatewayHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Defense-in-depth API key check, independent of the gateway's OAuth layer.
 *
 * <p>Register and login are exempt so a brand-new client can create an account. Every other
 * User Management endpoint requires {@code X-API-Key}, so a call that bypasses the gateway is
 * rejected even if the caller somehow has a bearer token.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ServiceSecurityProperties properties;
    private final ObjectMapper objectMapper;
    private final RequestMatcher exempt;

    public ApiKeyAuthFilter(ServiceSecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.exempt = new OrRequestMatcher(Arrays.stream(SecurityConfig.PUBLIC_PATHS)
                .map(AntPathRequestMatcher::new)
                .toArray(RequestMatcher[]::new));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return exempt.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String provided = request.getHeader(GatewayHeaders.API_KEY);
        if (provided == null || !provided.equals(properties.getApiKey())) {
            SecurityConfig.writeError(objectMapper, response, HttpStatus.UNAUTHORIZED,
                    "A valid X-API-Key header is required", request.getRequestURI());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
