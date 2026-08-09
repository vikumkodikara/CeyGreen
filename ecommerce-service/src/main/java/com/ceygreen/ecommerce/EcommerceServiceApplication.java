package com.ceygreen.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CeyGreen E-Commerce Marketplace microservice.
 *
 * <p>Farmers list harvest products; buyers browse and checkout. Publishes order
 * and stock events to Kafka for the analytics service to consume.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class EcommerceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceServiceApplication.class, args);
    }
}