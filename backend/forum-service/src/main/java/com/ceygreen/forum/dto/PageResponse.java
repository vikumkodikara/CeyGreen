package com.ceygreen.forum.dto;

import java.util.List;

/**
 * A single page of results plus pagination metadata, returned by list endpoints. A small explicit
 * shape is used instead of Spring's {@code Page} so the JSON contract is stable and documented.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }
}
