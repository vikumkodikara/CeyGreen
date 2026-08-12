package com.ceygreen.forum.repository;

/** Ordering options for the post listing endpoint. */
public enum PostSort {
    /** Most recently created first (default). */
    NEWEST,
    /** Highest total reply upvotes first. */
    MOST_UPVOTED;

    /** Map the API's {@code sort} query value to an enum, defaulting to {@link #NEWEST}. */
    public static PostSort fromParam(String value) {
        if (value == null) {
            return NEWEST;
        }
        return switch (value.trim().toLowerCase()) {
            case "mostupvoted", "most_upvoted", "upvotes" -> MOST_UPVOTED;
            default -> NEWEST;
        };
    }
}
