package com.ceygreen.iot.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ceygreen.security")
public class ServiceSecurityProperties {

    /**
     * Shared secret required in the {@code X-API-Key} header.
     * Must match the gateway's {@code DOWNSTREAM_API_KEY} / {@code SERVICE_API_KEY}.
     */
    private String apiKey = "ceygreen-dev-api-key";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
