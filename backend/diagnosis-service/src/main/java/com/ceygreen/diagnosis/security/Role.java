package com.ceygreen.diagnosis.security;

/**
 * The account roles this service recognises in an access token's {@code role} claim.
 *
 * <p>A local copy of the issuer's enum, deliberately: reading it from a shared jar would couple
 * Disease Detection's build to User Management. Registration concerns (which roles may be
 * self-registered) stay with the issuer, so they are absent here.
 */
public enum Role {

    FARMER,
    BUYER,
    ADMIN
}
