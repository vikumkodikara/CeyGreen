package com.ceygreen.diagnosis.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen.jwt")
public class JwtProperties {

    /**
     * X.509 PEM of User Management's signing key. This service only ever verifies, so it holds
     * the public half and nothing else — there is no private key to leak here.
     */
    @NotNull
    private Resource publicKeyLocation;

    @NotNull
    private String issuer;

    public Resource getPublicKeyLocation() {
        return publicKeyLocation;
    }

    public void setPublicKeyLocation(Resource publicKeyLocation) {
        this.publicKeyLocation = publicKeyLocation;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
