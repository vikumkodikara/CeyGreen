package com.ceygreen.forum.service;

import com.ceygreen.forum.common.ApiException;
import com.ceygreen.forum.dto.PageResponse;
import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
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
