package com.ceygreen.userdiagnosis.security;

import com.ceygreen.userdiagnosis.users.Role;
import com.ceygreen.userdiagnosis.users.User;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * Mints the RS256 access token returned by /users/login.
 *
 * <p>The token carries the caller's identity as claims so that no downstream service ever
 * needs to call User Management to ask who a user is: a FARMER token carries {@code farmerId},
 * a BUYER token carries {@code buyerId}, and both carry {@code role}.
 */
@Component
public class JwtIssuer {

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_FARMER_ID = "farmerId";
    public static final String CLAIM_BUYER_ID = "buyerId";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public JwtIssuer(JwtEncoder jwtEncoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public IssuedToken issue(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.getAccessTokenTtl());

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(CLAIM_ROLE, user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        if (user.getRole() == Role.FARMER) {
            claims.put(CLAIM_FARMER_ID, user.getId().toString());
        } else if (user.getRole() == Role.BUYER) {
            claims.put(CLAIM_BUYER_ID, user.getId().toString());
        }

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(properties.getIssuer())
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claims(existing -> existing.putAll(claims))
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).keyId(JwtKeyConfig.KEY_ID).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet)).getTokenValue();

        return new IssuedToken(token, properties.getAccessTokenTtl().toSeconds());
    }

    public record IssuedToken(String value, long expiresInSeconds) {
    }
}
