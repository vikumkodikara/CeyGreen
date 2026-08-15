package com.ceygreen.ecommerce;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class ProductControllerTest {

    private static final String API_KEY = "ceygreen-dev-api-key";
    private static final UUID FARMER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID BUYER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndListActiveProducts() throws Exception {
        mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "cropName", "Tomato",
                                "quantity", 15,
                                "unitPrice", 120.50,
                                "harvestDate", LocalDate.of(2026, 8, 5),
                                "location", "Kandy"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cropName").value("Tomato"))
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get("/products")
                        .header("X-API-Key", API_KEY)
                        .param("cropName", "Tomato")
                        .param("location", "Kandy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void buyerCannotCreateProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "BUYER")
                        .header("X-Buyer-Id", BUYER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "cropName", "Tomato",
                                "quantity", 15,
                                "unitPrice", 120.50,
                                "harvestDate", LocalDate.of(2026, 8, 5),
                                "location", "Kandy"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProductByIdReturns404WhenMissing() throws Exception {
        mockMvc.perform(get("/products/999999")
                        .header("X-API-Key", API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProductCanMarkInactive() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "cropName", "Beans",
                "quantity", 8,
                "unitPrice", 90.00,
                "harvestDate", LocalDate.of(2026, 8, 6),
                "location", "Galle"));

        String response = mockMvc.perform(post("/products")
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/products/" + id)
                        .header("X-API-Key", API_KEY)
                        .header("X-User-Role", "FARMER")
                        .header("X-Farmer-Id", FARMER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(get("/products")
                        .header("X-API-Key", API_KEY)
                        .param("cropName", "Beans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}