package com.example.task330.discussion.dto;

// Данные реакции, возвращаемые клиенту.
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

    // Получение идентификатора реакции.
    public Long getId() {
        return id;
    }

    // Изменение идентификатора реакции.
    public void setId(Long id) {
        this.id = id;
    }

    // Получение идентификатора темы.
    public Long getTopicId() {
        return topicId;
    }

    // Изменение идентификатора темы.
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    // Получение содержимого реакции.
    public String getContent() {
        return content;
    }

    // Изменение содержимого реакции.
    public void setContent(String content) {
        this.content = content;
    }

    // Получение состояния реакции.
    public String getState() {
        return state == null ? "APPROVE" : state;
    }

    // Изменение состояния реакции.
    public void setState(String state) {
        this.state = state;
    }
}
