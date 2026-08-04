package com.ceygreen.userdiagnosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 2 microservice: User Management (PostgreSQL) and Disease Detection
 * (MongoDB), fronted by the separately deployed api-gateway.
 *
 * <p>Per the "Service Independence" rule in the project spec, this service never issues a
 * REST call to another internal microservice. Its only outbound integration is publishing
 * to the {@code diagnosis-events} Kafka topic, fire-and-forget, with no reply expected.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class UserDiagnosisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDiagnosisServiceApplication.class, args);
    }
}
