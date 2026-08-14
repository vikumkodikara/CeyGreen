package com.ceygreen.userdiagnosis.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Proves the Service Independence rule: this microservice never calls another service's REST
 * API. The only legal outbound integration is the Kafka producer for diagnosis-events.
 */
class ServiceIndependenceGuardTest {

    private static final List<String> FORBIDDEN = List.of(
            "org.springframework.web.client.RestTemplate",
            "org.springframework.web.reactive.function.client.WebClient",
            "org.springframework.web.client.RestClient",
            "java.net.http.HttpClient");

    @Test
    void serviceSourceDoesNotUseSynchronousHttpClients() throws IOException {
        Path root = Path.of("src/main/java").toAbsolutePath().normalize();
        assertThat(root).exists();

        try (Stream<Path> paths = Files.walk(root)) {
            List<Path> javaFiles = paths.filter(path -> path.toString().endsWith(".java")).toList();
            assertThat(javaFiles).isNotEmpty();

            for (Path file : javaFiles) {
                String source = Files.readString(file);
                for (String forbidden : FORBIDDEN) {
                    assertThat(source)
                            .as("%s must not reference %s", file.getFileName(), forbidden)
                            .doesNotContain(forbidden);
                }
                // Catch simple bean names even without the fully-qualified import.
                assertThat(source).as(file.getFileName().toString())
                        .doesNotContain("new RestTemplate")
                        .doesNotContain("RestClient.builder")
                        .doesNotContain("WebClient.builder")
                        .doesNotContain("WebClient.create");
            }
        }
    }
}
