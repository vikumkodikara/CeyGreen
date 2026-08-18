package com.ceygreen.ai.common;

/**
 * Headers the API Gateway injects after it has validated the OAuth 2.0 bearer token. The gateway
 * strips any client-supplied copies first, so these are trustworthy exactly when the request also
 * carries the shared API key that only the gateway knows.
 */
public final class GatewayHeaders {
    public static final String API_KEY = "X-API-Key";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLE = "X-User-Role";
    public static final String USER_NAME = "X-User-Name";
    public static final String FARMER_ID = "X-Farmer-Id";
    public static final String BUYER_ID = "X-Buyer-Id";
    private GatewayHeaders() {}
}
