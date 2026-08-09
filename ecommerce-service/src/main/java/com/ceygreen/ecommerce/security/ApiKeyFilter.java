package com.ceygreen.ecommerce.security;

import com.ceygreen.ecommerce.common.ApiError;
import com.ceygreen.ecommerce.common.GatewayHeaders;
import com.ceygreen.ecommerce.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Requires {@code X-API-Key} on every marketplace endpoint except health and docs.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ServiceSecurityProperties properties;
    private final ObjectMapper objectMapper;
    private final RequestMatcher exempt;

    public ApiKeyFilter(ServiceSecurityProperties properties, ObjectMapper objectMapper) {
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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String provided = request.getHeader(GatewayHeaders.API_KEY);
        if (provided == null || !provided.equals(properties.getApiKey())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getOutputStream(),
                    ApiError.of(
                            HttpStatus.UNAUTHORIZED.value(),
                            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "A valid X-API-Key header is required",
                            request.getRequestURI()));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
