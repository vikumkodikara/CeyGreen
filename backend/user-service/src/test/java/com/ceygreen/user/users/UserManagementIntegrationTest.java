package com.ceygreen.user.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ceygreen.user.common.GatewayHeaders;
import com.ceygreen.user.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class UserManagementIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registersAFarmerAndNeverReturnsThePasswordHash() throws Exception {
        String email = uniqueEmail("farmer");

        String body = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nimal Perera",
                                  "email": "%s",
                                  "password": "greenhouse123",
                                  "role": "FARMER",
                                  "farmLocation": "Nuwara Eliya",
                                  "contactInfo": "+94771234567"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andExpect(jsonPath("$.farmLocation").value("Nuwara Eliya"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("password");
        assertThat(body).doesNotContain("$2a$");

        UUID id = UUID.fromString(objectMapper.readTree(body).get("id").asText());
        String storedHash = userRepository.findById(id).orElseThrow().getPasswordHash();
        assertThat(storedHash).startsWith("$2a$12$");
        assertThat(storedHash).isNotEqualTo("greenhouse123");
    }

    @Test
    void rejectsDuplicateEmailWithConflict() throws Exception {
        String email = uniqueEmail("duplicate");
        register(email, "FARMER");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson("Someone Else", email, "anotherpass1", "BUYER")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("An account already exists for this email address"));
    }

    @Test
    void rejectsInvalidRegistrationPayload() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "A", "email": "not-an-email", "password": "short", "role": "FARMER"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void refusesToSelfRegisterAnAdmin() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson("Root", uniqueEmail("admin"), "adminpass123", "ADMIN")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only FARMER or BUYER accounts can be self-registered"));
    }

    @Test
    void issuesATokenCarryingTheFarmerIdAndRole() throws Exception {
        String email = uniqueEmail("login");
        UUID userId = register(email, "FARMER");

        String body = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "greenhouse123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600))
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andReturn().getResponse().getContentAsString();

        JsonNode claims = decodeClaims(objectMapper.readTree(body).get("access_token").asText());
        assertThat(claims.get("sub").asText()).isEqualTo(userId.toString());
        assertThat(claims.get("role").asText()).isEqualTo("FARMER");
        assertThat(claims.get("farmerId").asText()).isEqualTo(userId.toString());
        assertThat(claims.has("buyerId")).isFalse();
    }

    @Test
    void issuesABuyerIdClaimForBuyerAccounts() throws Exception {
        String email = uniqueEmail("buyer");
        UUID userId = register(email, "BUYER");

        JsonNode claims = decodeClaims(login(email));
        assertThat(claims.get("buyerId").asText()).isEqualTo(userId.toString());
        assertThat(claims.has("farmerId")).isFalse();
    }

    @Test
    void rejectsWrongPasswordAndUnknownEmailIdentically() throws Exception {
        String email = uniqueEmail("wrongpass");
        register(email, "FARMER");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "definitely-wrong"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "nobody-here@ceygreen.test", "password": "definitely-wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void readsAProfileWithAValidToken() throws Exception {
        String email = uniqueEmail("profile");
        UUID userId = register(email, "FARMER");
        String token = login(email);

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void rejectsProfileReadWithoutAToken() throws Exception {
        UUID userId = register(uniqueEmail("notoken"), "FARMER");

        mockMvc.perform(get("/users/{id}", userId)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsNotFoundForAnUnknownUser() throws Exception {
        String token = loginAfterRegistering(uniqueEmail("missing"), "FARMER");

        mockMvc.perform(get("/users/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + token)
                        .header(GatewayHeaders.API_KEY, API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatesOwnProfile() throws Exception {
        String email = uniqueEmail("update");
        UUID userId = register(email, "FARMER");
        String token = login(email);

        mockMvc.perform(put("/users/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                        .header(GatewayHeaders.API_KEY, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Nimal K. Perera", "farmLocation": "Bandarawela", "contactInfo": "+94770000000"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nimal K. Perera"))
                .andExpect(jsonPath("$.farmLocation").value("Bandarawela"))
                .andExpect(jsonPath("$.contactInfo").value("+94770000000"))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void refusesToUpdateSomeoneElsesProfile() throws Exception {
        UUID victimId = register(uniqueEmail("victim"), "FARMER");
        String attackerToken = loginAfterRegistering(uniqueEmail("attacker"), "FARMER");

        mockMvc.perform(put("/users/{id}", victimId)
                        .header("Authorization", "Bearer " + attackerToken)
                        .header(GatewayHeaders.API_KEY, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Hijacked"}
                                """))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(victimId).orElseThrow().getName()).isNotEqualTo("Hijacked");
    }

    @Test
    void publishesTheSigningKeyAsAJwkSet() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                .andExpect(jsonPath("$.keys[0].kid").value("ceygreen-user-diagnosis"))
                .andExpect(jsonPath("$.keys[0].d").doesNotExist());
    }

    // ---- helpers ----

    private static String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@ceygreen.test";
    }

    private static String registrationJson(String name, String email, String password, String role) {
        return """
                {"name": "%s", "email": "%s", "password": "%s", "role": "%s"}
                """.formatted(name, email, password, role);
    }

    private UUID register(String email, String role) throws Exception {
        String body = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson("Test User", email, "greenhouse123", role)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    private String login(String email) throws Exception {
        String body = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "greenhouse123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("access_token").asText();
    }

    private String loginAfterRegistering(String email, String role) throws Exception {
        register(email, role);
        return login(email);
    }

    private JsonNode decodeClaims(String jwt) throws Exception {
        String payload = jwt.split("\\.")[1];
        return objectMapper.readTree(java.util.Base64.getUrlDecoder().decode(payload));
    }
}
