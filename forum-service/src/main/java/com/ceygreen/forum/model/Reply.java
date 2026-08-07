package com.ceygreen.forum.model;

import java.time.Instant;
import java.util.UUID;

public class Reply {
    private String id = UUID.randomUUID().toString();
    private String content;
    private String authorId;
    private String authorName;
    private Instant createdAt = Instant.now();

    public Reply() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
