package com.ceygreen.gateway.support;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import org.springframework.core.io.ClassPathResource;

public final class TestJwtSupport {

    private TestJwtSupport() {
    }

    public static String farmerToken(UUID farmerId) {
        return mint(farmerId, "FARMER", "farmerId", farmerId.toString());
    }

    public static String mint(UUID subject, String role, String extraClaim, String extraValue) {
        try {
            RSAPrivateKey privateKey = loadPrivateKey();
            Instant now = Instant.now();
            JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                    .issuer("https://ceygreen.local/auth")
                    .subject(subject.toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(3600)))
                    .claim("role", role);
            if (extraClaim != null) {
                claims.claim(extraClaim, extraValue);
            }
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("ceygreen-user-diagnosis").build(),
                    claims.build());
            jwt.sign(new RSASSASigner(privateKey));
            return jwt.serialize();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to mint test JWT", ex);
        }
    }

    private static RSAPrivateKey loadPrivateKey() throws Exception {
        String pem = new ClassPathResource("keys/dev-private.pem")
                .getContentAsString(StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
}
