package com.ceygreen.ecommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class OrderManagementTest {

    private static final String API_KEY = "ceygreen-dev-api-key";
    private static final UUID FARMER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID BUYER_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buyerCanListOrdersAndFarmerCanAdvanceStatus() throws Exception {
        Long productId = createProduct();

        mockMvc.perform(post("/orders/checkout")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "productId", productId,
                                "quantity", 1,
                                "buyerName", "Buyer",
                                "phone", "0771111111",
                                "address", "1 Road",
                                "city", "Colombo",
                                "postalCode", "00100"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orders[0].status").value("PENDING"));

        mockMvc.perform(get("/orders/my-orders")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        Long orderId = extractOrderId();

        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(get("/orders/farmer")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(orderId.intValue()));
    }

    private Long extractOrderId() throws Exception {
        String response = mockMvc.perform(get("/orders/my-orders")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("content").get(0).get("id").asLong();
    }

    private Long createProduct() throws Exception {
        String response = mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "cropName", "OrderMgmtCrop",
                                "quantity", 5,
                                "unitPrice", 100.00,
                                "harvestDate", LocalDate.of(2026, 8, 13),
                                "location", "Kandy"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}
