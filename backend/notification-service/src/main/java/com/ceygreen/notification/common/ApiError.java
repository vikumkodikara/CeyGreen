package com.ceygreen.notification.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Instant timestamp, int status, String error, String message, String path,
        Map<String, String> fieldErrors) {
    public static ApiError of(int s, String e, String m, String p) { return new ApiError(Instant.now(), s, e, m, p, null); }
}
