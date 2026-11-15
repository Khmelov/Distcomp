package com.example.task310.dto;

// Данные реакции, которые возвращаются клиенту.
public class ReactionResponseTo {

    private Long id;
    private Long topicId;
    private String content;

    // Конструктор без параметров.
    public ReactionResponseTo() {
    }

    // Конструктор для заполнения всех полей ответа.
    public ReactionResponseTo(Long id, Long topicId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
    }

    // Методы доступа к полям DTO.
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