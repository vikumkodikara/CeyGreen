package com.ceygreen.forum.repository;

import com.ceygreen.forum.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByCropTypeIgnoreCase(String cropType);
    List<Post> findByAuthorId(String authorId);
    List<Post> findAllByOrderByCreatedAtDesc();
}
