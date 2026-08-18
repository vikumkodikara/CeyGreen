package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumEventDto {
    private String userId;
    private String authorName;
    private String postId;
    private String topic;
    private String content;
    private String eventType;

    public ForumEventDto() {
    }

    public ForumEventDto(String userId, String authorName, String postId, String topic, String content, String eventType) {
        this.userId = userId;
        this.authorName = authorName;
        this.postId = postId;
        this.topic = topic;
        this.content = content;
        this.eventType = eventType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public static class Builder {
        private String userId;
        private String authorName;
        private String postId;
        private String topic;
        private String content;
        private String eventType;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder postId(String postId) {
            this.postId = postId;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ForumEventDto build() {
            return new ForumEventDto(userId, authorName, postId, topic, content, eventType);
        }
    }
}
