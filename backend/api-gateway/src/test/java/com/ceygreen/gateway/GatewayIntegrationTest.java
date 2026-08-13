package com.ceygreen.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceygreen.gateway.filter.IdentityHeaderGatewayFilter;
import com.ceygreen.gateway.support.TestJwtSupport;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayIntegrationTest {

    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static final MockWebServer downstream;

    static {
        REDIS.start();
        try {
            downstream = new MockWebServer();
            downstream.start();
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @AfterAll
    static void stopDownstream() throws IOException {
        downstream.shutdown();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("USER_DIAGNOSIS_SERVICE_URL", () -> downstream.url("/").toString().replaceAll("/$", ""));
        registry.add("ceygreen.rate-limit.requests-per-minute", () -> "60");
        registry.add("ceygreen.rate-limit.replenish-rate", () -> "60");
        registry.add("ceygreen.rate-limit.burst-capacity", () -> "3600");
        registry.add("ceygreen.rate-limit.requested-tokens", () -> "60");
        registry.add("ceygreen.downstream.api-key", () -> "ceygreen-dev-api-key");
        registry.add("ceygreen.cors.allowed-origins", () -> "http://localhost:3000");
    }

    @Test
    void rejectsProtectedRoutesWithoutAToken() {
        webTestClient.mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri("/api/users/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void forwardsAuthenticatedRequestsWithIdentityAndApiKeyHeaders() throws Exception {
        UUID farmerId = UUID.randomUUID();
        String token = TestJwtSupport.farmerToken(farmerId);

        downstream.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"id\":\"" + farmerId + "\",\"name\":\"Nimal\"}"));

        webTestClient.mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri("/api/users/" + farmerId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(IdentityHeaderGatewayFilter.HEADER_USER_ID, UUID.randomUUID().toString())
                .header(IdentityHeaderGatewayFilter.HEADER_API_KEY, "spoofed-key")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(farmerId.toString());

        RecordedRequest recorded = downstream.takeRequest(5, TimeUnit.SECONDS);
        assertThat(recorded).isNotNull();
        assertThat(recorded.getPath()).isEqualTo("/users/" + farmerId);
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_API_KEY))
                .isEqualTo("ceygreen-dev-api-key");
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_USER_ID))
                .isEqualTo(farmerId.toString());
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_USER_ROLE)).isEqualTo("FARMER");
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_FARMER_ID))
                .isEqualTo(farmerId.toString());
    }

    @Test
    void allowsPublicRegisterWithoutAToken() throws Exception {
        downstream.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"id\":\"" + UUID.randomUUID() + "\",\"email\":\"a@b.com\"}"));

        webTestClient.mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Nimal","email":"a@b.com","password":"greenhouse123","role":"FARMER"}
                        """)
                .exchange()
                .expectStatus().isCreated();

        RecordedRequest recorded = downstream.takeRequest(5, TimeUnit.SECONDS);
        assertThat(recorded).isNotNull();
        assertThat(recorded.getPath()).isEqualTo("/users/register");
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_API_KEY))
                .isEqualTo("ceygreen-dev-api-key");
        assertThat(recorded.getHeader(IdentityHeaderGatewayFilter.HEADER_USER_ID)).isNull();
    }

    @Test
    void corsConfigurationAllowsTheConfiguredClientOrigin(
            @Autowired org.springframework.web.cors.reactive.CorsConfigurationSource corsConfigurationSource) {
        var exchange = org.springframework.mock.http.server.reactive.MockServerHttpRequest
                .get("http://localhost/api/users/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .build();
        var config = corsConfigurationSource.getCorsConfiguration(
                org.springframework.mock.web.server.MockServerWebExchange.from(exchange));

        assertThat(config).isNotNull();
        assertThat(config.checkOrigin("http://localhost:3000")).isEqualTo("http://localhost:3000");
        assertThat(config.checkHttpMethod(HttpMethod.POST)).contains(HttpMethod.POST);
    }
}
