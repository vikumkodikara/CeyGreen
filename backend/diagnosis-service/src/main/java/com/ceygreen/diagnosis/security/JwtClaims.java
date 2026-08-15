package com.ceygreen.diagnosis.security;

/**
 * The claim names this service reads out of an access token.
 *
 * <p>Each microservice owns its own copy of the claim vocabulary rather than sharing a jar with
 * the issuer. That keeps Disease Detection deployable and testable with no build-time or
 * run-time dependency on User Management — the contract is the token, not a shared library.
 */
public final class JwtClaims {

    /** Carries the account's {@link Role}; the authorities converter maps it to ROLE_*. */
    public static final String CLAIM_ROLE = "role";

    private JwtClaims() {
    }
}
