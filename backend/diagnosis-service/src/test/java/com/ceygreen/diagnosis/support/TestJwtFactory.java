package com.ceygreen.diagnosis.support;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.converter.RsaKeyConverters;

/**
 * Mints access tokens for tests the same way user-service does in production.
 *
 * <p>Disease Detection has no login endpoint of its own — it only verifies tokens — so
 * integration tests cannot register-and-login to obtain one. Signing here with the dev private
 * key reproduces exactly what a real deployment sends: an RS256 token from a *different*
 * service, verified locally against {@code keys/dev-public.pem}.
 *
 * <p>The private key lives in {@code src/test/resources} only. It is never packaged into the
 * application jar, so the running service still cannot sign anything — which is the point of
 * the split.
 */
public final class TestJwtFactory {

    /** Matches ceygreen.jwt.issuer in application.yml. */
    public static final String ISSUER = "https://ceygreen.local/auth";

    /** Matches JwtKeyConfig.KEY_ID in user-service. */
    private static final String KEY_ID = "ceygreen-user-diagnosis";

    private static final String PRIVATE_KEY_LOCATION = "keys/dev-private.pem";

    private TestJwtFactory() {
    }

    /** A FARMER token for {@code userId}, valid for one hour. */
    public static String farmerToken(UUID userId) {
        return token(userId, "FARMER");
    }

    public static String token(UUID userId, String role) {
        Instant issuedAt = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject(userId.toString())
                .claim("role", role)
                .claim("email", "farmer-" + userId + "@ceygreen.test")
                .claim("name", "Diag Farmer")
                .claim("farmerId", userId.toString())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(issuedAt.plusSeconds(3600)))
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KEY_ID).build(), claims);
        try {
            JWSSigner signer = new RSASSASigner(privateKey());
            jwt.sign(signer);
        } catch (JOSEException ex) {
            throw new IllegalStateException("Failed to sign the test access token", ex);
        }
        return jwt.serialize();
    }

    private static RSAPrivateKey privateKey() {
        try (InputStream pem = new ClassPathResource(PRIVATE_KEY_LOCATION).getInputStream()) {
            return RsaKeyConverters.pkcs8().convert(pem);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read " + PRIVATE_KEY_LOCATION, ex);
        }
    }
}
