package com.ceygreen.iot.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI iotOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen IoT Telemetry API")
                        .version("1.0.0")
                        .description("""
                                Greenhouse blueprints, ESP32 ingest, rule-engine suggestions, \
                                and zone thresholds. Storage is Firebase Realtime Database \
                                (or in-memory when FIREBASE_ENABLED=false).

                                JWT is not used. Send header X-API-Key. HIGH rule hits publish \
                                to Kafka topic greenhouse-alerts. This service never calls \
                                another microservice over REST.""")
                        .license(new License().name("Coursework use")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Direct to iot-service (local)"),
                        new Server().url("http://localhost:8080/api").description("Through API Gateway (local)"),
                        new Server().url("http://16.192.168.12:8082").description("Direct to iot-service (EC2)"),
                        new Server().url("http://16.192.168.12:8080/api").description("Through API Gateway (EC2)")))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("Shared service key. Dev default: ceygreen-dev-api-key")));
    }
}
