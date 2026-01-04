package com.blog.model;

public class Message {
    private Long id;
    private Long topicId;
    private String content;

    public Message() {}

    public Message(Long id, Long topicId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}