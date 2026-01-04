package com.blog.discussion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageRequestTo {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    // Конструкторы
    public MessageRequestTo() {}

    public MessageRequestTo(Long topicId, String content) {
        this.topicId = topicId;
        this.content = content;
    }

    // Геттеры и сеттеры
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