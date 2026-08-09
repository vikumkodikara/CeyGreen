package com.ceygreen.gateway.config;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Objects;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /**
     * Shared Redis token bucket wired from {@code ceygreen.rate-limit.*} so tests and runtime
     * use the same limiter configuration.
     */
    @Bean
    public RedisRateLimiter redisRateLimiter(GatewayProperties properties) {
        GatewayProperties.RateLimit limit = properties.getRateLimit();
        return new RedisRateLimiter(
                limit.getReplenishRate(), limit.getBurstCapacity(), limit.getRequestedTokens());
    }

    /**
     * Token-bucket key for authenticated traffic: one bucket per principal name (JWT subject).
     * Falls back to client IP for public routes such as register/login.
     */
    @Bean
    public KeyResolver principalNameKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                .filter(name -> !name.isBlank())
                .switchIfEmpty(Mono.fromSupplier(() -> resolveClientIp(exchange)));
    }

    /**
     * Token-bucket key: one bucket per client IP. Referenced from application.yml as
     * {@code #{@clientIpKeyResolver}}.
     */
    @Bean
    @Primary
    public KeyResolver clientIpKeyResolver() {
        return exchange -> Mono.just(resolveClientIp(exchange));
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
