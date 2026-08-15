package com.ceygreen.forum.controller;

import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import com.ceygreen.forum.service.SimilarityService;
import com.ceygreen.forum.kafka.ForumEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/internal/posts")
public class InternalForumController {

    private final PostRepository postRepository;
    private final SimilarityService similarityService;
    private final ForumEventPublisher eventPublisher;

    public InternalForumController(PostRepository postRepository, SimilarityService similarityService, ForumEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.similarityService = similarityService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/unanswered")
    public ResponseEntity<List<Post>> getUnansweredPosts(@RequestParam int hours) {
        Instant cutoff = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<Post> posts = postRepository.findUnansweredBefore(cutoff, 50);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/similar")
    public ResponseEntity<Post> findSimilarPost(@RequestBody Post post) {
        Optional<Post> similar = similarityService.findSimilarResolvedPost(post);
        return similar.map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null)); // Need a non-404 to avoid exceptions in RestTemplate on null
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<Post> addInternalReply(@PathVariable String id, @RequestBody Reply reply) {
        return postRepository.findById(id).map(post -> {
            post.setAiAnswerAttempted(true);
            post.getReplies().add(reply);
            post.setUpdatedAt(Instant.now());
            Post saved = postRepository.save(post);
            eventPublisher.publishNewReply(saved, reply);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}
