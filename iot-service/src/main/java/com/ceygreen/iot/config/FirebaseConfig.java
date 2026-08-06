package com.ceygreen.iot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Initializes the Firebase Admin SDK for Realtime Database access.
 *
 * <p>Expects a service account JSON file at the path specified by
 * {@code GOOGLE_APPLICATION_CREDENTIALS}, or falls back to application-default credentials
 * when running on GCP.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${ceygreen.firebase.database-url}")
    private String databaseUrl;

    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("Firebase already initialized");
            return;
        }

        try {
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            FirebaseOptions.Builder builder = FirebaseOptions.builder()
                    .setDatabaseUrl(databaseUrl);

            if (credentialsPath != null && !credentialsPath.isBlank()) {
                builder.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)));
                log.info("Firebase initialized with service account from {}", credentialsPath);
            } else {
                builder.setCredentials(GoogleCredentials.getApplicationDefault());
                log.info("Firebase initialized with application-default credentials");
            }

            FirebaseApp.initializeApp(builder.build());
        } catch (IOException e) {
            log.warn("Firebase initialization failed — service will start but Firebase operations will fail: {}",
                    e.getMessage());
        }
    }
}
