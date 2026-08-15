package com.ceygreen.user.common;

import org.springframework.http.HttpStatus;

/** A failure that maps directly onto an HTTP status and a client-safe message. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException payloadTooLarge(String message) {
        return new ApiException(HttpStatus.PAYLOAD_TOO_LARGE, message);
    }

    public static ApiException unsupportedMediaType(String message) {
        return new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }
}
