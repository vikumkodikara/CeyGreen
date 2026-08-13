package com.ceygreen.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"order-events", "stock-events"})
class StockEventKafkaTest {

    private static final String API_KEY = "ceygreen-dev-api-key";
    private static final UUID FARMER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID BUYER_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void restockEventPublishedToKafkaOnQuantityIncrease() throws Exception {
        Long productId = createProduct(3);
        try (Consumer<String, String> consumer = createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "stock-events");

            mockMvc.perform(put("/products/" + productId)
                            .header("X-API-Key", API_KEY)
                            .header("X-User-Role", "FARMER")
                            .header("X-Farmer-Id", FARMER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantity\":20}"))
                    .andExpect(status().isOk());

            JsonNode payload = awaitStockEvent(consumer, productId, "RESTOCKED");
            assertThat(payload.get("previousQuantity").asInt()).isEqualTo(3);
            assertThat(payload.get("currentQuantity").asInt()).isEqualTo(20);
        }
    }

    @Test
    void lowStockEventPublishedToKafkaOnCheckout() throws Exception {
        Long productId = createProduct(12);
        try (Consumer<String, String> consumer = createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "stock-events");

            mockMvc.perform(post("/orders/checkout")
                            .header("X-API-Key", API_KEY)
                            .header("X-User-Role", "BUYER")
                            .header("X-Buyer-Id", BUYER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "productId", productId,
                                    "quantity", 5,
                                    "buyerName", "Test Buyer",
                                    "phone", "0771234567",
                                    "address", "123 Main Street",
                                    "city", "Colombo",
                                    "postalCode", "00100"))))
                    .andExpect(status().isCreated());

            JsonNode payload = awaitStockEvent(consumer, productId, "LOW_STOCK");
            assertThat(payload.get("previousQuantity").asInt()).isEqualTo(12);
            assertThat(payload.get("currentQuantity").asInt()).isEqualTo(7);
        }
    }

    private JsonNode awaitStockEvent(Consumer<String, String> consumer, Long productId, String eventType)
            throws Exception {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                JsonNode payload = objectMapper.readTree(record.value());
                if (eventType.equals(payload.get("eventType").asText())
                        && productId.equals(payload.get("productId").asLong())) {
                    return payload;
                }
            }
        }
        throw new AssertionError("No " + eventType + " event received for product " + productId);
    }

    private Consumer<String, String> createConsumer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "stock-event-test-" + UUID.randomUUID(), "true", embeddedKafka);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<String, String>(props).createConsumer();
    }

    private Long createProduct(int quantity) throws Exception {
        String response = mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "cropName", "StockKafkaCrop-" + UUID.randomUUID(),
                                "quantity", quantity,
                                "unitPrice", 50.00,
                                "harvestDate", LocalDate.of(2026, 8, 8),
                                "location", "Matara"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}