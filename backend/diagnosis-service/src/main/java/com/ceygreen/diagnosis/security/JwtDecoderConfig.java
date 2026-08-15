package com.ceygreen.diagnosis.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Loads User Management's public key and exposes the decoder that verifies access tokens.
 *
 * <p>Verification only — this service mints nothing, so there is no {@code JwtEncoder} and no
 * private key on its classpath. The key is read from a local PEM rather than fetched from the
 * issuer's JWKS endpoint, which is what keeps startup independent: Disease Detection boots and
 * serves traffic even when User Management is down, and no internal REST call is made
 * (CLAUDE.md §3).
 *
 * <p>A decoder lives here as well as on the gateway so the service can be run and demoed
 * standalone: hit it directly on port 8087 with a bearer token and it validates the token itself.
 */
@Configuration
public class JwtDecoderConfig {

    private final RSAPublicKey publicKey;
    private final String issuer;

    public JwtDecoderConfig(JwtProperties properties) throws IOException {
        try (InputStream publicKeyStream = properties.getPublicKeyLocation().getInputStream()) {
            this.publicKey = RsaKeyConverters.x509().convert(publicKeyStream);
        }
        this.issuer = properties.getIssuer();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), new JwtIssuerValidator(issuer)));
        return decoder;
    }
}
