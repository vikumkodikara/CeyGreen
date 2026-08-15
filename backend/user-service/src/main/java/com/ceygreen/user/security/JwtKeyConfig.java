package com.ceygreen.user.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Loads the RSA keypair and exposes the encoder that mints access tokens plus the decoder
 * that verifies them.
 *
 * <p>A decoder lives here as well as on the gateway so this service can be run and demoed
 * completely standalone: hit it directly on port 8081 with a bearer token and it validates
 * the token itself, no gateway required.
 */
@Configuration
public class JwtKeyConfig {

    /**
     * Stable key identifier. Kept at its original value across the service split: the keypair,
     * issuer and {@code kid} are unchanged, so tokens minted before and after the split remain
     * interchangeable for every verifier in the platform.
     */
    static final String KEY_ID = "ceygreen-user-diagnosis";

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final String issuer;

    public JwtKeyConfig(JwtProperties properties) throws IOException {
        try (InputStream privateKeyStream = properties.getPrivateKeyLocation().getInputStream();
             InputStream publicKeyStream = properties.getPublicKeyLocation().getInputStream()) {
            this.privateKey = RsaKeyConverters.pkcs8().convert(privateKeyStream);
            this.publicKey = RsaKeyConverters.x509().convert(publicKeyStream);
        }
        this.issuer = properties.getIssuer();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(KEY_ID).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), new JwtIssuerValidator(issuer)));
        return decoder;
    }

    RSAPublicKey publicKey() {
        return publicKey;
    }
}
