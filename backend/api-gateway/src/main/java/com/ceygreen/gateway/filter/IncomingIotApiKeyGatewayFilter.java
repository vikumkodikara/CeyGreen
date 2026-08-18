package com.ceygreen.gateway.filter;

import com.ceygreen.gateway.config.GatewayProperties;
import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public IoT routes skip JWT, so the client {@code X-API-Key} must be checked before
 * {@link IdentityHeaderGatewayFilter} replaces it with the gateway key.
 */
@Component
public class IncomingIotApiKeyGatewayFilter implements GlobalFilter, Ordered {

    private final GatewayProperties properties;

    public IncomingIotApiKeyGatewayFilter(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }
        String path = exchange.getRequest().getURI().getPath();
        if (!isIotPath(path)) {
            return chain.filter(exchange);
        }
        String provided = exchange.getRequest().getHeaders().getFirst(IdentityHeaderGatewayFilter.HEADER_API_KEY);
        String expected = properties.getDownstream().getApiKey();
        if (provided != null && provided.equals(expected)) {
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = """
                {"status":401,"error":"Unauthorized","message":"A valid X-API-Key header is required"}
                """.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    static boolean isIotPath(String path) {
        if (path == null) {
            return false;
        }
        String normalized = path.toLowerCase();
        return normalized.contains("/iot/") || normalized.endsWith("/iot") || normalized.contains("/api/iot");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 40;
    }
}
