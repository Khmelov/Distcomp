package com.example.restApi.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentKafkaMessage {

    public enum Operation {
        CREATE, UPDATE, DELETE
    }

    @JsonProperty("id")
    private Long id;

    @JsonProperty("articleId")
    private Long articleId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("state")
    private String state;

    @JsonProperty("operation")
    private Operation operation;

    public CommentKafkaMessage() {}

    public CommentKafkaMessage(Long id, Long articleId, String content, String state, Operation operation) {
        this.id = id;
        this.articleId = articleId;
        this.content = content;
        this.state = state;
        this.operation = operation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Operation getOperation() { return operation; }
    public void setOperation(Operation operation) { this.operation = operation; }
}
