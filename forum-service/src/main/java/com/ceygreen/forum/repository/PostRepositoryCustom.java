package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom post queries that the derived-query mechanism can't express: dynamic optional filters
 * combined with pagination, plus a "most upvoted" ordering that sums each post's embedded reply
 * upvotes. Implemented by {@code PostRepositoryImpl} and mixed into {@link PostRepository}.
 */
public interface PostRepositoryCustom {

    Page<Post> search(PostSearch search, Pageable pageable);
}
