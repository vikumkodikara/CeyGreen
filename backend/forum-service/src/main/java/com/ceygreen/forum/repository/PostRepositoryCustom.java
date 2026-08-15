package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

/**
 * Custom post queries that the derived-query mechanism can't express: dynamic optional filters
 * combined with pagination, plus a "most upvoted" ordering that sums each post's embedded reply
 * upvotes. Implemented by {@code PostRepositoryImpl} and mixed into {@link PostRepository}.
 */
public interface PostRepositoryCustom {

    Page<Post> search(PostSearch search, Pageable pageable);

    /**
     * Posts eligible for the AI fallback: not yet AI-attempted, created before {@code cutoff}, and
     * with no human (non-AI) reply. Oldest first, capped at {@code limit}.
     */
    List<Post> findUnansweredBefore(Instant cutoff, int limit);
}
