package com.ceygreen.diagnosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DiagnosisServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiagnosisServiceApplication.class, args);
    }
}
