package com.ceygreen.ai.security;

import com.ceygreen.ai.common.ApiError;
import com.ceygreen.ai.common.GatewayHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Rejects any request that does not carry this service's API key, then publishes the gateway's
 * forwarded identity as the request's {@link CurrentUser} principal.
 *
 * <p>The API key is what makes the identity headers trustworthy: the gateway strips client-supplied
 * copies and re-injects its own, so a request holding the key can only have come through it.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private final ServiceSecurityProperties properties;
    private final ObjectMapper objectMapper;

    public ApiKeyFilter(ServiceSecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                              FilterChain filterChain) throws ServletException, IOException {
        String provided = request.getHeader(GatewayHeaders.API_KEY);
        if (provided == null || !provided.equals(properties.getApiKey())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(),
                    ApiError.of(401, "Unauthorized", "A valid X-API-Key header is required", request.getRequestURI()));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate(request));
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private static UsernamePasswordAuthenticationToken authenticate(HttpServletRequest request) {
        String userId = header(request, GatewayHeaders.USER_ID);
        if (userId == null) {
            userId = header(request, GatewayHeaders.FARMER_ID);
        }
        if (userId == null) {
            userId = header(request, GatewayHeaders.BUYER_ID);
        }
        String role = header(request, GatewayHeaders.USER_ROLE);
        // The gateway forwards no display-name header, so fall back to the id rather than calling
        // User Management, which the service-independence rule forbids.
        String displayName = header(request, GatewayHeaders.USER_NAME);
        CurrentUser user = new CurrentUser(userId, displayName != null ? displayName : userId, role);

        List<GrantedAuthority> authorities = role == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)));
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    private static String header(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return value == null || value.isBlank() ? null : value;
    }
}
