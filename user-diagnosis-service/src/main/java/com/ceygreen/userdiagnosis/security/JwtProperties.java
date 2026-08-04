package com.ceygreen.userdiagnosis.security;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen.jwt")
public class JwtProperties {

    /** PKCS#8 PEM used to sign access tokens. Never leaves this service. */
    @NotNull
    private Resource privateKeyLocation;

    /** X.509 PEM of the matching public key, also configured on the gateway for validation. */
    @NotNull
    private Resource publicKeyLocation;

    @NotNull
    private String issuer;

    @NotNull
    private Duration accessTokenTtl = Duration.ofHours(1);

    public Resource getPrivateKeyLocation() {
        return privateKeyLocation;
    }

    public void setPrivateKeyLocation(Resource privateKeyLocation) {
        this.privateKeyLocation = privateKeyLocation;
    }

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

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }
}
