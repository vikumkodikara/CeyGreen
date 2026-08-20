package com.ceygreen.forum.config;

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
    public OpenAPI forumOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CeyGreen Forum API")
                        .version("1.0.0")
                        .description("Community posts and replies. X-API-Key required. Internal routes are not public.")
                        .license(new License().name("Coursework use")))
                .servers(List.of(
                        new Server().url("http://localhost:8085").description("Direct (local)"),
                        new Server().url("http://localhost:8080/api").description("Gateway (local)"),
                        new Server().url("http://16.192.168.12:8085").description("Direct (EC2)"),
                        new Server().url("http://16.192.168.12:8080/api").description("Gateway (EC2)")))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")));
    }
}
