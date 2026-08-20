package com.ceygreen.treatment_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen Treatment API")
                        .version("1.0.0")
                        .description("Remedies by disease and crop. X-API-Key required."))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Direct (local)"),
                        new Server().url("http://localhost:8080/api").description("Gateway (local)"),
                        new Server().url("http://16.192.168.12:8083").description("Direct (EC2)"),
                        new Server().url("http://16.192.168.12:8080/api").description("Gateway (EC2)")))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .name("X-API-Key")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }
}
