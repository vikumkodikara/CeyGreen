package com.ceygreen.forum.controller;

import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumController {
    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    /** List posts, optionally filtered by category. */
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> listPosts(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(forumService.listPosts(category));
    }

    /** Get a single post with its full reply thread. */
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String id) {
        return ResponseEntity.ok(forumService.getPost(id));
    }

    /** Create a new discussion post. */
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.createPost(request));
    }

    /** Add a reply to an existing post. */
    @PostMapping("/posts/{id}/replies")
    public ResponseEntity<PostResponse> addReply(@PathVariable String id, @Valid @RequestBody ReplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.addReply(id, request));
    }

    /** Delete a post (author or admin). */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        forumService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
