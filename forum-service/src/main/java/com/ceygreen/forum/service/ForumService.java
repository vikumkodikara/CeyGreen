package com.ceygreen.forum.service;

import com.ceygreen.forum.common.ApiException;
import com.ceygreen.forum.dto.PostRequest;
import com.ceygreen.forum.dto.PostResponse;
import com.ceygreen.forum.dto.ReplyRequest;
import com.ceygreen.forum.kafka.ForumEventPublisher;
import com.ceygreen.forum.model.Post;
import com.ceygreen.forum.model.Reply;
import com.ceygreen.forum.repository.PostRepository;
import com.ceygreen.forum.security.CurrentUser;
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

    public List<PostResponse> listPosts(String cropType) {
        List<Post> posts = (cropType != null && !cropType.isBlank())
                ? postRepository.findByCropTypeIgnoreCase(cropType)
                : postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream().map(this::toListResponse).toList();
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
        return new PostResponse(p.getId(), p.getAuthorId(), p.getAuthorName(), p.getTitle(), p.getBody(),
                p.getTags(), p.getCropType(), p.isResolved(), p.getAcceptedReplyId(), p.isFlagged(),
                p.getFlagCount(), p.isAiAnswerAttempted(), List.of(),
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
