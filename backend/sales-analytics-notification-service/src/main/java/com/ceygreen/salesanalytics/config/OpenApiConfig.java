package com.ceygreen.salesanalytics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8086}")
    private String serverPort;

    @Value("${ceygreen.security.api-key-header:X-API-KEY}")
    private String apiKeyHeader;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen Smart Greenhouse - Sales Analytics & Notification Service (Student 6)")
                        .version("1.0.0")
                        .description("Microservice for Student 6 in the CeyGreen Greenhouse project. " +
                                "Provides Kafka-driven Sales Aggregations, Farmer Analytics, and Multi-channel Notifications.")
                        .contact(new Contact()
                                .name("CeyGreen Engineering Team")
                                .email("dev@ceygreen.agritech.lk")
                                .url("https://ceygreen.agritech.lk"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(apiKeyHeader)
                                .description("Enter your CeyGreen API Key (default: `ceygreen-secret-api-key-2026`)")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
    }
}
