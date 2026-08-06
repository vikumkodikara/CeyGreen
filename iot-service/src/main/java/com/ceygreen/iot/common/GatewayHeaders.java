package com.ceygreen.iot.common;

/**
 * Headers the API Gateway attaches after it validates the OAuth 2.0 access token.
 */
public final class GatewayHeaders {

    public static final String API_KEY = "X-API-Key";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLE = "X-User-Role";
    public static final String FARMER_ID = "X-Farmer-Id";

    private GatewayHeaders() {
    }
}
