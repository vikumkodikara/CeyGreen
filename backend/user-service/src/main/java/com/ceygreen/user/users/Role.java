package com.ceygreen.user.users;

/**
 * Distinguishes what the client shows a user, and which identity claim their access token
 * carries: FARMER accounts get a {@code farmerId} claim, BUYER accounts a {@code buyerId}.
 */
public enum Role {

    FARMER,
    BUYER,
    ADMIN;

    /** ADMIN accounts are provisioned, never self-registered through /users/register. */
    public boolean isSelfRegisterable() {
        return this == FARMER || this == BUYER;
    }
}
