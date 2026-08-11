package com.ceygreen.forum.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A forum discussion post with its replies embedded, so a full thread loads in a single read.
 *
 * <p>{@code authorName} is denormalised from the identity forwarded by the API Gateway. This
 * service never calls User Management to resolve a display name, so a post keeps the name the
 * author had at write time.
 */
@Document(collection = "posts")
@CompoundIndex(name = "resolved_createdAt_idx", def = "{'resolved': 1, 'createdAt': -1}")
public class Post {
    @Id private String id;
    @Indexed private String authorId;
    private String authorName;
    private String title;
    private String body;
    @Indexed private List<String> tags = new ArrayList<>();
    @Indexed private String cropType;
    private boolean resolved;
    private String acceptedReplyId;
    private boolean flagged;
    private int flagCount;
    private boolean aiAnswerAttempted;
    private List<Reply> replies = new ArrayList<>();
    @Indexed(direction = IndexDirection.DESCENDING) private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public Post() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public String getAcceptedReplyId() { return acceptedReplyId; }
    public void setAcceptedReplyId(String acceptedReplyId) { this.acceptedReplyId = acceptedReplyId; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public int getFlagCount() { return flagCount; }
    public void setFlagCount(int flagCount) { this.flagCount = flagCount; }
    public boolean isAiAnswerAttempted() { return aiAnswerAttempted; }
    public void setAiAnswerAttempted(boolean aiAnswerAttempted) { this.aiAnswerAttempted = aiAnswerAttempted; }
    public List<Reply> getReplies() { return replies; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    /** Find an embedded reply by its id. */
    public Reply findReply(String replyId) {
        return replies.stream().filter(r -> Objects.equals(r.getId(), replyId)).findFirst().orElse(null);
    }
}
