package com.ceygreen.gateway.web;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * One Swagger UI for every microservice. Specs are fetched through the gateway
 * ({@code /docs/openapi/...}) so the browser does not need each service port.
 */
@RestController
public class ApiDocsHubController {

    @GetMapping("/swagger-ui.html")
    public Mono<Void> swaggerUiRedirect(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/docs"));
        return exchange.getResponse().setComplete();
    }

    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> hub() {
        return Mono.just("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8"/>
                  <title>CeyGreen API docs</title>
                  <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5.17.14/swagger-ui.css"/>
                  <style>
                    body { margin: 0; background: #0b3d2e; }
                    .hub { font-family: Segoe UI, sans-serif; color: #eef8f1; padding: 1.25rem 1.5rem 0; }
                    .hub h1 { margin: 0 0 0.35rem; font-size: 1.4rem; }
                    .hub p { margin: 0 0 1rem; color: #b7dcc6; max-width: 52rem; }
                    code { color: #cfe8d8; }
                    #swagger-ui { background: #fff; }
                  </style>
                </head>
                <body>
                  <div class="hub">
                    <h1>CeyGreen OpenAPI / Swagger UI</h1>
                    <p>Use the <strong>Select a definition</strong> dropdown for every service.
                    Authorize with <code>X-API-Key: ceygreen-dev-api-key</code>. User and diagnosis also need a Bearer JWT.
                    If a spec fails to load, that service is not running.</p>
                  </div>
                  <div id="swagger-ui"></div>
                  <script src="https://unpkg.com/swagger-ui-dist@5.17.14/swagger-ui-bundle.js"></script>
                  <script src="https://unpkg.com/swagger-ui-dist@5.17.14/swagger-ui-standalone-preset.js"></script>
                  <script>
                    window.ui = SwaggerUIBundle({
                      urls: [
                        { name: "User Management (8081)", url: "/docs/openapi/users" },
                        { name: "IoT Telemetry (8082)", url: "/docs/openapi/iot-telemetry" },
                        { name: "Treatment (8083)", url: "/docs/openapi/treatment" },
                        { name: "E-Commerce (8084)", url: "/docs/openapi/ecommerce" },
                        { name: "Forum (8085)", url: "/docs/openapi/forum" },
                        { name: "Analytics (8086)", url: "/docs/openapi/analytics" },
                        { name: "Disease Detection (8087)", url: "/docs/openapi/diagnosis" },
                        { name: "Notifications (8088)", url: "/docs/openapi/notifications" }
                      ],
                      "urls.primaryName": "User Management (8081)",
                      dom_id: "#swagger-ui",
                      presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
                      layout: "StandaloneLayout",
                      deepLinking: true,
                      filter: true,
                      persistAuthorization: true,
                      validatorUrl: null
                    });
                  </script>
                </body>
                </html>
                """);
    }
}
