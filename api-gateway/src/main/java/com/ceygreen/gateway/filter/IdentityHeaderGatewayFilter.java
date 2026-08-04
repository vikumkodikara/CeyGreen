package com.ceygreen.gateway.filter;

import com.ceygreen.gateway.config.GatewayProperties;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Strips any client-supplied identity headers, then injects the authenticated claims plus the
 * shared downstream API key. Downstream services trust these headers only because the request
 * also carries the API key that only the gateway knows.
 */
@Component
public class IdentityHeaderGatewayFilter implements GlobalFilter, Ordered {

    public static final String HEADER_API_KEY = "X-API-Key";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_FARMER_ID = "X-Farmer-Id";
    public static final String HEADER_BUYER_ID = "X-Buyer-Id";

    private static final List<String> STRIPPED = List.of(
            HEADER_API_KEY, HEADER_USER_ID, HEADER_USER_ROLE, HEADER_FARMER_ID, HEADER_BUYER_ID);

    private final GatewayProperties properties;

    public IdentityHeaderGatewayFilter(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        STRIPPED.forEach(header -> builder.headers(headers -> headers.remove(header)));
        builder.header(HEADER_API_KEY, properties.getDownstream().getApiKey());

        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> withIdentity(builder, jwt).build())
                .defaultIfEmpty(builder.build())
                .flatMap(request -> chain.filter(exchange.mutate().request(request).build()));
    }

    private static ServerHttpRequest.Builder withIdentity(ServerHttpRequest.Builder builder, Jwt jwt) {
        builder.header(HEADER_USER_ID, jwt.getSubject());
        String role = jwt.getClaimAsString("role");
        if (role != null) {
            builder.header(HEADER_USER_ROLE, role);
        }
        String farmerId = jwt.getClaimAsString("farmerId");
        if (farmerId != null) {
            builder.header(HEADER_FARMER_ID, farmerId);
        }
        String buyerId = jwt.getClaimAsString("buyerId");
        if (buyerId != null) {
            builder.header(HEADER_BUYER_ID, buyerId);
        }
        return builder;
    }

    @Override
    public int getOrder() {
        // After security has authenticated the exchange, before the Netty routing filter.
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
