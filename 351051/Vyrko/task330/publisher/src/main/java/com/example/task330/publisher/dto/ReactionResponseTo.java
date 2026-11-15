package com.example.task330.publisher.dto;

// Данные реакции, которые возвращаются клиенту.
public class ReactionResponseTo {

    private Long id;
    private Long topicId;
    private String content;
    private String state;

    // Конструктор без параметров.
    public ReactionResponseTo() {
    }

    // Старый конструктор для совместимости с Task330.
    public ReactionResponseTo(Long id, Long topicId, String content) {
        this(id, topicId, content, "APPROVE");
    }

    // Конструктор для заполнения всех полей ответа.
    public ReactionResponseTo(Long id, Long topicId, String content, String state) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.state = state;
    }

    // Возвращает идентификатор реакции.
    public Long getId() {
        return id;
    }

    // Устанавливает идентификатор реакции.
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает идентификатор темы.
    public Long getTopicId() {
        return topicId;
    }

    // Устанавливает идентификатор темы.
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    // Возвращает содержимое реакции.
    public String getContent() {
        return content;
    }

    // Устанавливает содержимое реакции.
    public void setContent(String content) {
        this.content = content;
    }

    // Возвращает состояние реакции.
    public String getState() {
        return state == null ? "APPROVE" : state;
    }

    // Устанавливает состояние реакции.
    public void setState(String state) {
        this.state = state;
    }
}
