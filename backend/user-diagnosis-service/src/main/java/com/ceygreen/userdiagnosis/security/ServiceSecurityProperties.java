package com.ceygreen.userdiagnosis.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen.security")
public class ServiceSecurityProperties {

    /**
     * Shared secret required in the X-API-Key header on every endpoint except register and
     * login. Enforced by this service itself, independently of the gateway's OAuth layer.
     */
    @NotBlank
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
