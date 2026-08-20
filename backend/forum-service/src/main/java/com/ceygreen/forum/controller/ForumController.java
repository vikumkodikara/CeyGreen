package com.ceygreen.forum.controller;

import com.ceygreen.forum.dto.PageResponse;
import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyActionRequest;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.dto.ReportRequest;
import com.ceygreen.forum.security.CurrentUser;
import com.ceygreen.forum.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/forum")
@Tag(name = "Forum")
@SecurityRequirement(name = "apiKey")
public class ForumController {
    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    /**
     * List posts with optional filters and pagination.
     *
     * @param tags     comma-separated tags; a post matches if it carries any of them
     * @param cropType exact crop type match (case-insensitive)
     * @param resolved filter by resolved state when provided
     * @param sort     {@code newest} (default) or {@code mostUpvoted}
     * @param page     zero-based page index
     * @param size     page size (clamped server-side)
     */
    @GetMapping("/posts")
    public ResponseEntity<PageResponse<PostResponse>> listPosts(
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(forumService.listPosts(tags, cropType, resolved, sort, page, size));
    }

    /** Get a single post with its full reply thread. */
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String id) {
        return ResponseEntity.ok(forumService.getPost(id));
    }

    /** Create a new discussion post. */
    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request,
                                                   @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.createPost(request, currentUser));
    }

    /**
     * Add a reply to a post, or apply a thread action (upvote / acceptAnswer / flag) to it. The
     * single endpoint distinguishes the two by the presence of an {@code action} field: a new reply
     * returns 201 Created, an action returns 200 OK.
     */
    @PostMapping("/posts/{id}/replies")
    public ResponseEntity<PostResponse> addReplyOrAct(@PathVariable String id,
                                                      @Valid @RequestBody ReplyActionRequest request,
                                                      @AuthenticationPrincipal CurrentUser currentUser) {
        if (request.isAction()) {
            return ResponseEntity.ok(forumService.applyReplyAction(id, request, currentUser));
        }
        PostResponse created = forumService.addReply(id, new ReplyRequest(request.body()), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Delete a post (author or admin). */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id,
                                           @AuthenticationPrincipal CurrentUser currentUser) {
        forumService.deletePost(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    /** Report a post or thread. */
    @PostMapping("/posts/{id}/report")
    public ResponseEntity<Void> reportPost(@PathVariable String id,
                                           @Valid @RequestBody ReportRequest request,
                                           @AuthenticationPrincipal CurrentUser currentUser) {
        forumService.reportPost(id, request, currentUser);
        return ResponseEntity.ok().build();
    }
}
