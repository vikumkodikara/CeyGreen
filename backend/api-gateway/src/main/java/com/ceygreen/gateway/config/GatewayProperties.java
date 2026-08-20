package com.ceygreen.gateway.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen")
public class GatewayProperties {

    private final RateLimit rateLimit = new RateLimit();
    private final Auth auth = new Auth();
    private final Cors cors = new Cors();
    private final Downstream downstream = new Downstream();

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Auth getAuth() {
        return auth;
    }

    public Cors getCors() {
        return cors;
    }

    public Downstream getDownstream() {
        return downstream;
    }

    public static class RateLimit {
        @Min(1)
        private int requestsPerMinute = 60;
        @Min(1)
        private int replenishRate = 60;
        @Min(1)
        private int burstCapacity = 3600;
        @Min(1)
        private int requestedTokens = 60;

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }

        public int getReplenishRate() {
            return replenishRate;
        }

        public void setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }

        public int getRequestedTokens() {
            return requestedTokens;
        }

        public void setRequestedTokens(int requestedTokens) {
            this.requestedTokens = requestedTokens;
        }
    }

    public static class Auth {
        @NotEmpty
        private List<String> publicPaths = new ArrayList<>(List.of(
                "/api/users/register",
                "/api/users/login",
                "/api/iot/**",
                "/docs",
                "/actuator/health",
                "/actuator/health/**",
                "/actuator/info"));

        public List<String> getPublicPaths() {
            return publicPaths;
        }

        public void setPublicPaths(List<String> publicPaths) {
            this.publicPaths = publicPaths;
        }
    }

    public static class Cors {
        @NotBlank
        private String allowedOrigins = "http://localhost:3000,http://localhost:5173";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Downstream {
        @NotBlank
        private String apiKey = "ceygreen-dev-api-key";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
