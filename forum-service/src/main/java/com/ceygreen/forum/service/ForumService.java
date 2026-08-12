package com.ceygreen.forum.service;

import com.ceygreen.forum.common.ApiException;
import com.ceygreen.forum.dto.PageResponse;
import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyActionRequest;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.kafka.ForumEventPublisher;
import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import com.ceygreen.forum.repository.PostSearch;
import com.ceygreen.forum.repository.PostSort;
import com.ceygreen.forum.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ForumService {
    private static final Logger log = LoggerFactory.getLogger(ForumService.class);
    private final PostRepository postRepository;
    private final ForumEventPublisher eventPublisher;

    public ForumService(PostRepository postRepository, ForumEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
    }

    // Query params are clamped to sane bounds so a caller can't request an unbounded page.
    private static final int MAX_PAGE_SIZE = 100;

    // Flags accumulate until this many, at which point the item is marked for moderation review.
    private static final int FLAG_THRESHOLD = 3;

    /**
     * List posts with optional filters (any-of tags, exact case-insensitive crop type, resolved
     * flag), ordering ({@code newest} or {@code mostUpvoted}) and pagination. List entries omit the
     * reply thread and expose {@code replyCount} only.
     */
    public PageResponse<PostResponse> listPosts(String tags, String cropType, Boolean resolved,
                                                String sort, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        PostSearch search = new PostSearch(parseTags(tags), cropType, resolved, PostSort.fromParam(sort));
        Page<Post> result = postRepository.search(search, PageRequest.of(safePage, safeSize));
        List<PostResponse> content = result.getContent().stream().map(this::toListResponse).toList();
        return PageResponse.of(content, safePage, safeSize, result.getTotalElements());
    }

    /** Split a comma-separated {@code tags} query value into trimmed, non-empty tokens. */
    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public PostResponse getPost(String id) {
        return toResponse(requirePost(id));
    }

    public PostResponse createPost(PostRequest request, CurrentUser user) {
        Post post = new Post();
        post.setAuthorId(user.requireUserId());
        post.setAuthorName(user.displayName());
        post.setTitle(request.title());
        post.setBody(request.body());
        post.setCropType(request.cropType());
        if (request.tags() != null) {
            post.setTags(request.tags());
        }
        Post saved = postRepository.save(post);
        log.info("Created post id={} by author={}", saved.getId(), saved.getAuthorId());
        return toResponse(saved);
    }

    public PostResponse addReply(String postId, ReplyRequest request, CurrentUser user) {
        if (request.body() == null || request.body().isBlank()) {
            throw ApiException.badRequest("Reply body is required");
        }
        Post post = requirePost(postId);

        Reply reply = new Reply();
        reply.setAuthorId(user.requireUserId());
        reply.setAuthorName(user.displayName());
        reply.setBody(request.body());
        post.getReplies().add(reply);
        post.setUpdatedAt(Instant.now());

        Post saved = postRepository.save(post);
        eventPublisher.publishNewReply(saved, reply);
        log.info("Added reply id={} to post={} by author={}", reply.getId(), postId, reply.getAuthorId());
        return toResponse(saved);
    }

    /**
     * Apply a thread action (upvote / acceptAnswer / flag) to a post or one of its replies. Unlike
     * reply creation this does not emit a Kafka event. Returns the updated post.
     */
    public PostResponse applyReplyAction(String postId, ReplyActionRequest request, CurrentUser user) {
        Post post = requirePost(postId);
        String userId = user.requireUserId();
        String action = request.action().trim();
        switch (action) {
            case "upvote" -> upvoteReply(post, request.replyId(), userId);
            case "acceptAnswer" -> acceptAnswer(post, request.replyId(), userId);
            case "flag" -> flag(post, request.replyId());
            default -> throw ApiException.badRequest("Unknown action: " + request.action());
        }
        post.setUpdatedAt(Instant.now());
        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    /** Add one upvote from this user, idempotently — a repeat vote is a no-op. */
    private void upvoteReply(Post post, String replyId, String userId) {
        Reply reply = requireReply(post, replyId);
        if (reply.getUpvotedBy().contains(userId)) {
            return;
        }
        reply.getUpvotedBy().add(userId);
        reply.setUpvotes(reply.getUpvotes() + 1);
        log.info("Upvoted reply id={} on post={} by user={}", replyId, post.getId(), userId);
    }

    /** Mark a reply as the accepted answer. Only the post's own author may do this. */
    private void acceptAnswer(Post post, String replyId, String userId) {
        if (!Objects.equals(post.getAuthorId(), userId)) {
            throw ApiException.forbidden("Only the post author may accept an answer");
        }
        Reply reply = requireReply(post, replyId);
        post.setResolved(true);
        post.setAcceptedReplyId(reply.getId());
        log.info("Accepted reply id={} as answer on post={}", reply.getId(), post.getId());
    }

    /** Increment the flag count on a reply, or on the post itself when no replyId is given. */
    private void flag(Post post, String replyId) {
        if (replyId == null || replyId.isBlank()) {
            post.setFlagCount(post.getFlagCount() + 1);
            if (post.getFlagCount() >= FLAG_THRESHOLD) {
                post.setFlagged(true);
            }
            log.info("Flagged post id={} (count={})", post.getId(), post.getFlagCount());
            return;
        }
        Reply reply = requireReply(post, replyId);
        reply.setFlagCount(reply.getFlagCount() + 1);
        if (reply.getFlagCount() >= FLAG_THRESHOLD) {
            reply.setFlagged(true);
        }
        log.info("Flagged reply id={} on post={} (count={})", replyId, post.getId(), reply.getFlagCount());
    }

    private Reply requireReply(Post post, String replyId) {
        if (replyId == null || replyId.isBlank()) {
            throw ApiException.badRequest("replyId is required for this action");
        }
        Reply reply = post.findReply(replyId);
        if (reply == null) {
            throw ApiException.notFound("Reply not found: " + replyId);
        }
        return reply;
    }

    /** Delete a post. Allowed for the post's author, or for an admin. */
    public void deletePost(String id, CurrentUser user) {
        Post post = requirePost(id);
        user.requireUserId();
        if (!user.canActOnBehalfOf(post.getAuthorId())) {
            throw ApiException.forbidden("Only the post author or an admin may delete this post");
        }
        postRepository.deleteById(id);
        log.info("Deleted post id={} by user={}", id, user.userId());
    }

    private Post requirePost(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Post not found: " + id));
    }

    private PostResponse toListResponse(Post p) {
        // List view carries no reply bodies — replies=null is omitted by @JsonInclude(NON_NULL),
        // leaving replyCount as the only reply-related field.
        return new PostResponse(p.getId(), p.getAuthorId(), p.getAuthorName(), p.getTitle(), p.getBody(),
                p.getTags(), p.getCropType(), p.isResolved(), p.getAcceptedReplyId(), p.isFlagged(),
                p.getFlagCount(), p.isAiAnswerAttempted(), null,
                p.getReplies() != null ? p.getReplies().size() : 0,
                p.getCreatedAt(), p.getUpdatedAt());
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(p.getId(), p.getAuthorId(), p.getAuthorName(), p.getTitle(), p.getBody(),
                p.getTags(), p.getCropType(), p.isResolved(), p.getAcceptedReplyId(), p.isFlagged(),
                p.getFlagCount(), p.isAiAnswerAttempted(), p.getReplies(),
                p.getReplies() != null ? p.getReplies().size() : 0,
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
