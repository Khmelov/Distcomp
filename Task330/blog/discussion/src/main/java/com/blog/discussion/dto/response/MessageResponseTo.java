package com.blog.discussion.dto.response;

import java.time.LocalDateTime;

public class MessageResponseTo {
    private String country;
    private Long topicId;
    private Long id;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    // Конструкторы
    public MessageResponseTo() {}

    public MessageResponseTo(String country, Long topicId, Long id, String content,
                             LocalDateTime created, LocalDateTime modified) {
        this.country = country;
        this.topicId = topicId;
        this.id = id;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    // Геттеры и сеттеры
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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