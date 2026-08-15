package com.ceygreen.userdiagnosis.common;

/**
 * Headers the API Gateway attaches after it validates the OAuth 2.0 access token.
 *
 * <p>The gateway strips any client-supplied copy of these before injecting its own, so a
 * caller cannot forge an identity by setting them by hand. They are only trusted because the
 * request also carried the shared service API key, which proves it came through the gateway.
 */
public final class GatewayHeaders {

    public static final String API_KEY = "X-API-Key";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLE = "X-User-Role";
    public static final String FARMER_ID = "X-Farmer-Id";
    public static final String BUYER_ID = "X-Buyer-Id";

    private GatewayHeaders() {
    }
}
