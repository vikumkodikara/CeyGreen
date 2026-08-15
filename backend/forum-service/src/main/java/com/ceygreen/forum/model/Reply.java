package com.ceygreen.forum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A reply embedded in a {@link Post}. Ids are generated client-side as UUIDs because MongoDB does
 * not assign ObjectIds to embedded subdocuments.
 *
 * <p>{@code upvotedBy} holds the ids of users who have upvoted, which is what makes upvoting
 * idempotent — a user cannot vote twice.
 */
public class Reply {
    private String id = UUID.randomUUID().toString();
    private String authorId;
    private String authorName;
    private String body;
    private boolean aiGenerated;
    private int upvotes;
    private List<String> upvotedBy = new ArrayList<>();
    private boolean flagged;
    private int flagCount;
    private Instant createdAt = Instant.now();

    public Reply() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    // Serialised as "isAiGenerated" to match the documented forum API and Kafka event contract.
    @JsonProperty("isAiGenerated")
    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }
    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public List<String> getUpvotedBy() { return upvotedBy; }
    public void setUpvotedBy(List<String> upvotedBy) { this.upvotedBy = upvotedBy; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public int getFlagCount() { return flagCount; }
    public void setFlagCount(int flagCount) { this.flagCount = flagCount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
