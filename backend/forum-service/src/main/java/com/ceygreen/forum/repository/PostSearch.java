package com.ceygreen.forum.repository;

import java.util.List;

/**
 * Filter and sort parameters for {@link PostRepositoryCustom#search}. Any field left null/empty is
 * treated as "no filter on this attribute".
 */
public record PostSearch(
        List<String> tags,
        String cropType,
        Boolean resolved,
        PostSort sort) {
}
