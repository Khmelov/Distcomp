package com.example.publisher.dto;

public class KafkaReactionRequest {
    private String id;
    private Long storyId;
    private String content;

    public KafkaReactionRequest() {}

    public KafkaReactionRequest(String id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}