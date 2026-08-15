package com.ceygreen.user.support;

import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared integration-test base: a real PostgreSQL in a container, so tests exercise the same
 * engine as production rather than an in-memory substitute.
 *
 * <p>The container is static and never stopped, so all test classes share one instance and one
 * Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {

    /** Matches the ceygreen.security.api-key default in application.yml. */
    protected static final String API_KEY = "ceygreen-dev-api-key";

    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("ceygreen_users")
            .withUsername("ceygreen")
            .withPassword("ceygreen")
            .withReuse(true);

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
