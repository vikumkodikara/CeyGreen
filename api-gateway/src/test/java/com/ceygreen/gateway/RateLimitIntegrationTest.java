package com.ceygreen.gateway;

import com.ceygreen.gateway.support.TestJwtSupport;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Dedicated Spring context with a one-token bucket so the second request must receive HTTP 429.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RateLimitIntegrationTest {

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

    @BeforeAll
    void warmUpJvmBeforeRateLimitAssertions() {
        // First request can trigger Mockito agent attachment; keep it off the rate-limit path.
        webTestClient.get().uri("/actuator/health").exchange();
    }

    @AfterAll
    static void stopDownstream() throws IOException {
        downstream.shutdown();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("USER_DIAGNOSIS_SERVICE_URL", () -> downstream.url("/").toString().replaceAll("/$", ""));
        // One request allowed, then 429 until the bucket replenishes.
        registry.add("ceygreen.rate-limit.requests-per-minute", () -> "1");
        registry.add("ceygreen.rate-limit.replenish-rate", () -> "1");
        registry.add("ceygreen.rate-limit.burst-capacity", () -> "1");
        registry.add("ceygreen.rate-limit.requested-tokens", () -> "1");
        registry.add("ceygreen.downstream.api-key", () -> "ceygreen-dev-api-key");
    }

    @Test
    void returnsTooManyRequestsWhenTheBucketIsEmpty() {
        UUID farmerId = UUID.randomUUID();
        String token = TestJwtSupport.farmerToken(farmerId);

        downstream.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{}"));
        downstream.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{}"));

        WebTestClient client = webTestClient.mutate().responseTimeout(Duration.ofSeconds(20)).build();

        client.get()
                .uri("/api/users/" + farmerId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        client.get()
                .uri("/api/users/" + farmerId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }
}
