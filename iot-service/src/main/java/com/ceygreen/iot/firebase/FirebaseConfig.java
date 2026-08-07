package com.ceygreen.iot.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Initializes Firebase Admin when {@code ceygreen.firebase.enabled=true}.
 * Keep disabled for local demo (in-memory repository is used instead).
 */
@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@ConditionalOnProperty(prefix = "ceygreen.firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try (InputStream credentials = openCredentials(properties.getCredentialsPath())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentials))
                    .setDatabaseUrl(properties.getDatabaseUrl())
                    .setProjectId(properties.getProjectId())
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase initialized for project {}", properties.getProjectId());
            return app;
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        return FirebaseDatabase.getInstance(firebaseApp);
    }

    private static InputStream openCredentials(String credentialsPath) throws IOException {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException(
                    "ceygreen.firebase.credentials-path (GOOGLE_APPLICATION_CREDENTIALS) is required");
        }
        return new FileInputStream(credentialsPath);
    }
}
