package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Standard CRUD comes from {@link MongoRepository}; the dynamic filtered/paginated listing lives in
 * {@link PostRepositoryCustom} (implemented by {@code PostRepositoryImpl}).
 */
public interface PostRepository extends MongoRepository<Post, String>, PostRepositoryCustom {
}
