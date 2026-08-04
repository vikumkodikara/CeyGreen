package com.ceygreen.userdiagnosis.diagnosis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ceygreen.userdiagnosis.common.GatewayHeaders;
import com.ceygreen.userdiagnosis.diagnosis.classifier.MockDiseaseClassifier;
import com.ceygreen.userdiagnosis.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

class DiagnosisIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @DynamicPropertySource
    static void diagnosisTestProperties(DynamicPropertyRegistry registry) {
        registry.add("disease.upload.directory",
                () -> System.getProperty("java.io.tmpdir") + "/ceygreen-diagnosis-test-" + UUID.randomUUID());
        registry.add("disease.upload.cache-identical-uploads", () -> "true");
    }

    @Test
    void uploadsAnImagePersistsItAndPublishesADiagnosisEvent() throws Exception {
        Farmer farmer = registerAndLogin();
        byte[] imageBytes = uniqueJpegBytes("healthy-leaf");

        try (KafkaConsumer<String, Map<String, Object>> consumer = diagnosisEventConsumer()) {
            consumer.subscribe(List.of("diagnosis-events"));
            // Force assignment before the publish so we do not miss the message.
            consumer.poll(Duration.ofMillis(100));

            String body = mockMvc.perform(multipart("/diagnosis/upload")
                            .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg", imageBytes))
                            .file(new MockMultipartFile("farmerId", "", "text/plain",
                                    farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                            .file(new MockMultipartFile("cropType", "", "text/plain",
                                    "tomato".getBytes(StandardCharsets.UTF_8)))
                            .header("Authorization", "Bearer " + farmer.token)
                            .header(GatewayHeaders.API_KEY, API_KEY))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.diagnosisId").exists())
                    .andExpect(jsonPath("$.farmerId").value(farmer.id.toString()))
                    .andExpect(jsonPath("$.cropType").value("tomato"))
                    .andExpect(jsonPath("$.predictedDisease").exists())
                    .andExpect(jsonPath("$.confidenceScore").isNumber())
                    .andExpect(jsonPath("$.imageUrl").exists())
                    .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(body);
            String diagnosisId = response.get("diagnosisId").asText();

            Diagnosis stored = diagnosisRepository.findById(diagnosisId).orElseThrow();
            assertThat(stored.getFarmerId()).isEqualTo(farmer.id);
            assertThat(stored.getCropType()).isEqualTo("tomato");
            assertThat(stored.getImageHash()).isNotBlank();

            await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
                List<ConsumerRecord<String, Map<String, Object>>> records = new ArrayList<>();
                consumer.poll(Duration.ofMillis(500)).forEach(records::add);
                assertThat(records).anySatisfy(record -> {
                    assertThat(record.key()).isEqualTo(diagnosisId);
                    assertThat(record.value().get("diagnosisId")).isEqualTo(diagnosisId);
                    assertThat(record.value().get("farmerId")).isEqualTo(farmer.id.toString());
                    assertThat(record.value().get("predictedDisease")).isNotNull();
                });
            });
        }
    }

    @Test
    void returnsUncertainWhenConfidenceIsBelowThreshold() throws Exception {
        Farmer farmer = registerAndLogin();
        byte[] lowConfidenceBytes = findBytesWithConfidenceBelow(0.6);

        mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg", lowConfidenceBytes))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "potato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.predictedDisease").value(DiagnosisService.UNCERTAIN_LABEL))
                .andExpect(jsonPath("$.confidenceScore").value(org.hamcrest.Matchers.lessThan(0.6)));
    }

    @Test
    void rejectsUnsupportedFileTypes() throws Exception {
        Farmer farmer = registerAndLogin();

        mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "notes.txt", "text/plain",
                                "not an image".getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "tomato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void rejectsFarmerIdMismatchWithForbidden() throws Exception {
        Farmer farmer = registerAndLogin();
        UUID someoneElse = UUID.randomUUID();

        mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg",
                                uniqueJpegBytes("mismatch")))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                someoneElse.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "tomato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isForbidden());
    }

    @Test
    void cachesIdenticalUploadsForTheSameFarmer() throws Exception {
        Farmer farmer = registerAndLogin();
        byte[] imageBytes = uniqueJpegBytes("cache-me");

        String first = mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg", imageBytes))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "tomato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String second = mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg", imageBytes))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "tomato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String firstId = objectMapper.readTree(first).get("diagnosisId").asText();
        String secondId = objectMapper.readTree(second).get("diagnosisId").asText();
        assertThat(secondId).isEqualTo(firstId);
        assertThat(diagnosisRepository.findAll()).filteredOn(d -> d.getFarmerId().equals(farmer.id))
                .hasSize(1);
    }

    @Test
    void listsHistoryAndDeletesADiagnosis() throws Exception {
        Farmer farmer = registerAndLogin();
        String diagnosisId = upload(farmer, uniqueJpegBytes("history"));

        mockMvc.perform(get("/diagnosis/history/{farmerId}", farmer.id)
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosisId").value(diagnosisId));

        mockMvc.perform(get("/diagnosis/{id}", diagnosisId)
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosisId").value(diagnosisId));

        mockMvc.perform(delete("/diagnosis/{id}", diagnosisId)
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isNoContent());

        assertThat(diagnosisRepository.findById(diagnosisId)).isEmpty();
    }

    @Test
    void rejectsProtectedEndpointsWithoutApiKey() throws Exception {
        Farmer farmer = registerAndLogin();

        mockMvc.perform(get("/diagnosis/history/{farmerId}", farmer.id)
                        .header("Authorization", "Bearer " + farmer.token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("A valid X-API-Key header is required"));
    }

    // ---- helpers ----

    private Farmer registerAndLogin() throws Exception {
        String email = "farmer-" + UUID.randomUUID() + "@ceygreen.test";
        String body = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Diag Farmer", "email": "%s", "password": "greenhouse123", "role": "FARMER"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(body).get("id").asText());

        String loginBody = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "greenhouse123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginBody).get("access_token").asText();
        return new Farmer(id, token);
    }

    private String upload(Farmer farmer, byte[] imageBytes) throws Exception {
        String body = mockMvc.perform(multipart("/diagnosis/upload")
                        .file(new MockMultipartFile("image", "leaf.jpg", "image/jpeg", imageBytes))
                        .file(new MockMultipartFile("farmerId", "", "text/plain",
                                farmer.id.toString().getBytes(StandardCharsets.UTF_8)))
                        .file(new MockMultipartFile("cropType", "", "text/plain",
                                "tomato".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", "Bearer " + farmer.token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("diagnosisId").asText();
    }

    private KafkaConsumer<String, Map<String, Object>> diagnosisEventConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "diagnosis-it-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap");
        return new KafkaConsumer<>(props);
    }

    private static byte[] uniqueJpegBytes(String seed) {
        // Not a real JPEG — the service validates Content-Type, not magic bytes.
        return ("fake-jpeg-" + seed + "-" + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] findBytesWithConfidenceBelow(double threshold) {
        MockDiseaseClassifier classifier = new MockDiseaseClassifier();
        for (int i = 0; i < 10_000; i++) {
            byte[] candidate = ("low-conf-" + i).getBytes(StandardCharsets.UTF_8);
            if (classifier.predict(candidate).confidenceScore() < threshold) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not find a low-confidence payload for the mock classifier");
    }

    private record Farmer(UUID id, String token) {
    }
}
