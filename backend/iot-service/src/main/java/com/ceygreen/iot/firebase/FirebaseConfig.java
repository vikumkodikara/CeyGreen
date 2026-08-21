package com.ceygreen.iot.firebase;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * Firebase Admin credentials plus HTTPS REST access to Realtime Database.
 * The SDK websocket listener hangs in Docker; REST uses the same database URL.
 */
@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@ConditionalOnProperty(prefix = "ceygreen.firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public GoogleCredentials firebaseCredentials(FirebaseProperties properties) throws IOException {
        if (properties.getDatabaseUrl() == null || properties.getDatabaseUrl().isBlank()) {
            throw new IllegalStateException("FIREBASE_DATABASE_URL is required when Firebase is enabled");
        }
        try (InputStream credentials = openCredentials(properties.getCredentialsPath())) {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentials)
                    .createScoped(List.of(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"));
            googleCredentials.refresh();
            log.info("Firebase REST client ready for project {}", properties.getProjectId());
            return googleCredentials;
        }
    }

    @Bean
    @Qualifier("firebaseRestClient")
    public RestClient firebaseRestClient(
            FirebaseProperties properties,
            GoogleCredentials firebaseCredentials,
            ObjectMapper objectMapper) {
        ObjectMapper firebaseMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        String baseUrl = properties.getDatabaseUrl().replaceAll("/+$", "");
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .messageConverters(converters -> {
                    converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                    converters.add(0, new MappingJackson2HttpMessageConverter(firebaseMapper));
                })
                .requestInterceptor((request, body, execution) -> {
                    firebaseCredentials.refreshIfExpired();
                    request.getHeaders().setBearerAuth(
                            firebaseCredentials.getAccessToken().getTokenValue());
                    return execution.execute(request, body);
                })
                .build();
    }

    private static InputStream openCredentials(String credentialsPath) throws IOException {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException(
                    "ceygreen.firebase.credentials-path (GOOGLE_APPLICATION_CREDENTIALS) is required");
        }
        return new FileInputStream(credentialsPath);
    }
}
