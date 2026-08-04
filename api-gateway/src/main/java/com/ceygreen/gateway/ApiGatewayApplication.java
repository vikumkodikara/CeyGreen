package com.ceygreen.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen API Gateway: the single entry point for every client request.
 *
 * <p>Validates OAuth 2.0 bearer tokens, applies the CORS allowlist and a Redis-backed
 * token-bucket rate limit, then forwards the request to the owning microservice with the
 * caller's identity attached as headers.
 *
 * <p>The gateway is the one intentional exception to the "no service calls another service"
 * rule: it is an entry proxy rather than a peer service. It holds no business logic and no
 * database.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
