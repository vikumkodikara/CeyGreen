package com.ceygreen.user.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Publishes the public half of the signing key in JWKS form.
 *
 * <p>Provided for completeness of the OAuth 2.0 story and for any external client that wants
 * to verify a token itself. Nothing in this system depends on it: the gateway is configured
 * with the public key directly, so it can validate tokens even when this service is down.
 */
@RestController
@Tag(name = "OAuth 2.0", description = "Token signing key discovery")
public class JwksController {

    private final RSAKey publicJwk;

    public JwksController(JwtKeyConfig keyConfig) {
        this.publicJwk = new RSAKey.Builder(keyConfig.publicKey()).keyID(JwtKeyConfig.KEY_ID).build();
    }

    @GetMapping(path = "/oauth2/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "JSON Web Key Set containing the access-token signing key")
    public Map<String, Object> jwks() {
        return new JWKSet(publicJwk).toJSONObject();
    }
}
