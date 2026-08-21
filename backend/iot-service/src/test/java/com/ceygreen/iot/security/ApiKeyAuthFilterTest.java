package com.ceygreen.iot.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyAuthFilterTest {

    @Test
    void acceptsExactDevKey() {
        assertTrue(ApiKeyAuthFilter.keysMatch("ceygreen-dev-api-key", "ceygreen-dev-api-key"));
    }

    @Test
    void rejectsWrongKey() {
        assertFalse(ApiKeyAuthFilter.keysMatch("wrong-key", "ceygreen-dev-api-key"));
    }

    @Test
    void rejectsMissingKey() {
        assertFalse(ApiKeyAuthFilter.keysMatch(null, "ceygreen-dev-api-key"));
        assertFalse(ApiKeyAuthFilter.keysMatch("", "ceygreen-dev-api-key"));
        assertFalse(ApiKeyAuthFilter.keysMatch("   ", "ceygreen-dev-api-key"));
    }
}
