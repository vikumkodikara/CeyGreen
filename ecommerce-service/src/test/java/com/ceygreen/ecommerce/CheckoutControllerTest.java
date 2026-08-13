package com.ceygreen.ecommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"order-events", "stock-events"})
class CheckoutControllerTest {

    private static final String API_KEY = "ceygreen-dev-api-key";
    private static final UUID FARMER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID BUYER_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Map<String, Object> checkoutBody(Long productId, int quantity) {
        return Map.of(
                "productId", productId,
                "quantity", quantity,
                "buyerName", "Test Buyer",
                "phone", "0771234567",
                "address", "123 Main Street",
                "city", "Colombo",
                "postalCode", "00100");
    }

    @Test
    void successfulCheckoutDecrementsStockAndCreatesOrder() throws Exception {
        Long productId = createProduct(10);

        mockMvc.perform(post("/orders/checkout")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutBody(productId, 3))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orders[0].quantity").value(3))
                .andExpect(jsonPath("$.orders[0].totalPrice").value(361.50))
                .andExpect(jsonPath("$.orders[0].status").value("PENDING"));
    }

    @Test
    void farmerCannotCheckout() throws Exception {
        Long productId = createProduct(5);

        mockMvc.perform(post("/orders/checkout")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutBody(productId, 1))))
                .andExpect(status().isForbidden());
    }

    @Test
    void insufficientStockIsRejected() throws Exception {
        Long productId = createProduct(2);

        mockMvc.perform(post("/orders/checkout")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutBody(productId, 5))))
                .andExpect(status().isBadRequest());
    }

    private Long createProduct(int quantity) throws Exception {
        String response = mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "cropName", "CheckoutCrop",
                                "quantity", quantity,
                                "unitPrice", 120.50,
                                "harvestDate", LocalDate.of(2026, 8, 7),
                                "location", "Colombo"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }
}