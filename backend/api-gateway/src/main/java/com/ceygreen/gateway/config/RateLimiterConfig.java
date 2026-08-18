package com.ceygreen.gateway.config;

import java.net.InetSocketAddress;
import java.util.Objects;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /**
     * Token-bucket key: one bucket per client IP. Referenced from application.yml as
     * {@code #{@clientIpKeyResolver}}.
     */
    @Bean
    public KeyResolver clientIpKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getURI().getPath();
            if (isIotPath(path)) {
                return Mono.empty();
            }
            return Mono.just(resolveClientIp(exchange));
        };
    }

    static boolean isIotPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        String normalized = path.toLowerCase();
        return normalized.contains("/iot/")
                || normalized.endsWith("/iot")
                || normalized.contains("/api/iot");
    }

    static String resolveClientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        InetSocketAddress remote = exchange.getRequest().getRemoteAddress();
        if (remote != null && remote.getAddress() != null) {
            return Objects.requireNonNullElse(remote.getAddress().getHostAddress(), "unknown");
        }
        return "unknown";
    }
}
