package com.ceygreen.forum.service;

import com.ceygreen.forum.common.ApiException;
import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.kafka.ForumEventPublisher;
import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ForumService {
    private static final Logger log = LoggerFactory.getLogger(ForumService.class);
    private final PostRepository postRepository;
    private final ForumEventPublisher eventPublisher;

    public ForumService(PostRepository postRepository, ForumEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<PostResponse> listPosts(String category) {
        List<Post> posts = (category != null && !category.isBlank())
                ? postRepository.findByCategoryIgnoreCase(category)
                : postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream().map(this::toResponse).toList();
    }

    public PostResponse getPost(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Post not found: " + id));
        return toResponse(post);
    }

    public PostResponse createPost(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setAuthorId(request.authorId());
        post.setAuthorName(request.authorName());
        post.setCategory(request.category());
        Post saved = postRepository.save(post);
        log.info("Created post id={}, title={}", saved.getId(), saved.getTitle());
        return toResponse(saved);
    }

    public PostResponse addReply(String postId, ReplyRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> ApiException.notFound("Post not found: " + postId));

        Reply reply = new Reply();
        reply.setContent(request.content());
        reply.setAuthorId(request.authorId());
        reply.setAuthorName(request.authorName());
        post.getReplies().add(reply);
        post.setUpdatedAt(Instant.now());

        Post saved = postRepository.save(post);
        eventPublisher.publishNewReply(postId, post.getTitle(), request.authorId());
        log.info("Added reply to post={} by author={}", postId, request.authorId());
        return toResponse(saved);
    }

    public void deletePost(String id) {
        if (!postRepository.existsById(id)) {
            throw ApiException.notFound("Post not found: " + id);
        }
        postRepository.deleteById(id);
        log.info("Deleted post id={}", id);
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(p.getId(), p.getTitle(), p.getContent(), p.getAuthorId(),
                p.getAuthorName(), p.getCategory(), p.getReplies(),
                p.getReplies() != null ? p.getReplies().size() : 0,
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
