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
            // StripPrefix=1 runs before RequestRateLimiter, so this may be "/iot/**" here.
            if (path.startsWith("/iot/") || path.equals("/iot")
                    || path.startsWith("/api/iot/") || path.equals("/api/iot")) {
                return Mono.empty();
            }
            return Mono.just(resolveClientIp(exchange));
        };
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
