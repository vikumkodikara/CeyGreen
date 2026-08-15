package com.ceygreen.ecommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(
        partitions = 1,
        topics = {"order-events", "stock-events"})
class EcommerceServiceApplicationTests {

    private static final String API_KEY = "ceygreen-dev-api-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {}

    @Test
    void healthIsPublicWithoutApiKey() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void productsRejectMissingApiKey() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isUnauthorized());
    }

    @Test
    void productsAcceptValidApiKey() throws Exception {
        mockMvc.perform(get("/products").header("X-API-Key", API_KEY)).andExpect(status().isOk());
    }
}
