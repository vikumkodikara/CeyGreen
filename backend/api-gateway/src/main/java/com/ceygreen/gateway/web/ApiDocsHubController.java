package com.ceygreen.gateway.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Public index of every microservice Swagger UI (OpenAPI 3 / SpringDoc).
 */
@RestController
public class ApiDocsHubController {

    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> hub() {
        return Mono.just("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8"/>
                  <title>CeyGreen API docs</title>
                  <style>
                    body { font-family: Sora, Segoe UI, sans-serif; background: #0b3d2e; color: #eef8f1; margin: 0; padding: 2.5rem; }
                    h1 { margin: 0 0 0.4rem; }
                    p { color: #b7dcc6; }
                    a { color: #cfe8d8; }
                    table { border-collapse: collapse; width: min(920px, 100%); background: #0f4d3a; border-radius: 16px; overflow: hidden; }
                    th, td { text-align: left; padding: 0.75rem 1rem; border-bottom: 1px solid #1f8a54; }
                    th { background: #176b41; }
                  </style>
                </head>
                <body>
                  <h1>CeyGreen OpenAPI / Swagger UI</h1>
                  <p>Interactive docs for every microservice. Authorize with header <code>X-API-Key: ceygreen-dev-api-key</code>. User and diagnosis also need a Bearer JWT from login.</p>
                  <table>
                    <tr><th>Service</th><th>Port</th><th>Swagger UI</th><th>OpenAPI JSON</th></tr>
                    <tr><td>User Management</td><td>8081</td><td><a href="http://localhost:8081/swagger-ui.html">UI</a></td><td><a href="http://localhost:8081/v3/api-docs">JSON</a></td></tr>
                    <tr><td>IoT Telemetry</td><td>8082</td><td><a href="http://localhost:8082/swagger-ui.html">UI</a></td><td><a href="http://localhost:8082/v3/api-docs">JSON</a></td></tr>
                    <tr><td>Treatment</td><td>8083</td><td><a href="http://localhost:8083/swagger-ui.html">UI</a></td><td><a href="http://localhost:8083/v3/api-docs">JSON</a></td></tr>
                    <tr><td>E-Commerce</td><td>8084</td><td><a href="http://localhost:8084/swagger-ui.html">UI</a></td><td><a href="http://localhost:8084/v3/api-docs">JSON</a></td></tr>
                    <tr><td>Forum</td><td>8085</td><td><a href="http://localhost:8085/swagger-ui.html">UI</a></td><td><a href="http://localhost:8085/v3/api-docs">JSON</a></td></tr>
                    <tr><td>Analytics</td><td>8086</td><td><a href="http://localhost:8086/swagger-ui.html">UI</a></td><td><a href="http://localhost:8086/v3/api-docs">JSON</a></td></tr>
                    <tr><td>Disease Detection</td><td>8087</td><td><a href="http://localhost:8087/swagger-ui.html">UI</a></td><td><a href="http://localhost:8087/v3/api-docs">JSON</a></td></tr>
                    <tr><td>Notifications</td><td>8088</td><td><a href="http://localhost:8088/swagger-ui.html">UI</a></td><td><a href="http://localhost:8088/v3/api-docs">JSON</a></td></tr>
                  </table>
                  <p>On AWS, replace localhost with <code>16.192.168.12</code>.</p>
                </body>
                </html>
                """);
    }
}
