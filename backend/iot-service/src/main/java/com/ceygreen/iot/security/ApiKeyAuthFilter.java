package com.ceygreen.iot.security;

import com.ceygreen.iot.common.ApiError;
import com.ceygreen.iot.common.GatewayHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Requires {@code X-API-Key} on every IoT endpoint except health and docs.
 * A missing, blank, or wrong key is 401. If Postman sends two X-API-Key headers
 * (visible + hidden collection auth), every value must match.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        List<String> provided = Collections.list(request.getHeaders(GatewayHeaders.API_KEY));
        String expected = properties.getApiKey();
        boolean valid = !provided.isEmpty() && provided.stream().allMatch(value -> keysMatch(value, expected));
        if (!valid) {
            log.warn(
                    "Rejected {} {} — X-API-Key missing or invalid ({} header value(s))",
                    request.getMethod(),
                    request.getRequestURI(),
                    provided.size());
            reject(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "iot-api-client",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_API")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    static boolean keysMatch(String provided, String expected) {
        if (provided == null || expected == null) {
            return false;
        }
        byte[] left = provided.trim().getBytes(StandardCharsets.UTF_8);
        byte[] right = expected.getBytes(StandardCharsets.UTF_8);
        if (left.length == 0 || right.length == 0) {
            return false;
        }
        return MessageDigest.isEqual(left, right);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiError.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "A valid X-API-Key header is required",
                        request.getRequestURI()));
    }
}
