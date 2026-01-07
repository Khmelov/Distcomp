package com.task310.discussion.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.task310.discussion.model.PostState;
import java.time.LocalDateTime;

public class PostKafkaResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("articleId")
    private Long articleId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("state")
    private PostState state;
    @JsonProperty("created")
    private LocalDateTime created;
    @JsonProperty("modified")
    private LocalDateTime modified;

    public PostKafkaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}

