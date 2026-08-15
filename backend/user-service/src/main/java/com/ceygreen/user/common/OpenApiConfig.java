package com.ceygreen.user.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen User Management API")
                        .version("1.0.0")
                        .description("""
                                Account registration, login and profile on PostgreSQL.

                                This service is the platform's OAuth 2.0 authorization server: it
                                mints the RS256 access tokens every other CeyGreen service verifies,
                                and publishes its signing key at /oauth2/jwks.

                                Two independent security layers apply. Every endpoint except
                                /users/register, /users/login and /oauth2/jwks needs both a valid
                                OAuth 2.0 bearer token and the shared X-API-Key header, so a request
                                that bypasses the gateway is rejected even if it carries a valid
                                token.

                                This service never calls another microservice.""")
                        .license(new License().name("Coursework use")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Through the API Gateway"),
                        new Server().url("http://localhost:8081").description("Direct to this service")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("OAuth 2.0 access token from POST /users/login"))
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("Per-service API key, checked independently of the token")));
    }
}
