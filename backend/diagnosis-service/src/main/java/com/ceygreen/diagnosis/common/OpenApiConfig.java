package com.ceygreen.diagnosis.common;

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
    public OpenAPI diagnosisOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen Disease Detection API")
                        .version("1.0.0")
                        .description("""
                                Leaf-image upload, ONNX inference and diagnosis history on MongoDB.

                                This service verifies the RS256 access tokens minted by User
                                Management using the matching public key. It never calls User
                                Management -- or any other CeyGreen service -- to resolve an
                                identity; the farmerId and role come from the token claims alone.

                                Two independent security layers apply. Every endpoint needs both a
                                valid OAuth 2.0 bearer token and the shared X-API-Key header, so a
                                request that bypasses the gateway is rejected even if it carries a
                                valid token.

                                The only outbound integration is the diagnosis-events Kafka topic,
                                published fire-and-forget.""")
                        .license(new License().name("Coursework use")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Through the API Gateway (Local)"),
                        new Server().url("http://localhost:8087").description("Direct to this service (Local)"),
                        new Server().url("http://16.192.168.12:8080/api").description("Through the API Gateway (AWS EC2)"),
                        new Server().url("http://16.192.168.12:8087").description("Direct to this service (AWS EC2)")))
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
