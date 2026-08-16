package com.ceygreen.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen User Management microservice: account registration, login and profile on
 * PostgreSQL, fronted by the separately deployed api-gateway.
 *
 * <p>This service is the platform's OAuth 2.0 authorization server — it owns the signing
 * keypair and mints the RS256 access tokens every other CeyGreen service verifies locally.
 *
 * <p>Per the "Service Independence" rule in the project spec, this service never issues a
 * REST call to another internal microservice.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
