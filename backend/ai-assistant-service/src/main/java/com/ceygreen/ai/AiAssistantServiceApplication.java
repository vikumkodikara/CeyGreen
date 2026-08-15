package com.ceygreen.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen Student 5 microservice: Community Forum.
 *
 * <p>Farmers and buyers create discussion posts and reply to threads. Posts are stored
 * as MongoDB documents with embedded replies. New reply events are published to the
 * {@code forum-events} Kafka topic.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AiAssistantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiAssistantServiceApplication.class, args);
    }
}
