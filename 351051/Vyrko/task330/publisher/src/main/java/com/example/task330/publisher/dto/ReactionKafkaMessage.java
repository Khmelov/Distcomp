
package com.example.task330.publisher.dto;

import java.util.List;

// Сообщение для обмена реакциями между Publisher и Discussion через Kafka.
public class ReactionKafkaMessage {

    private String requestId;
    private String operation;
    private Long reactionId;
    private Long topicId;
    private Long id;
    private String content;
    private String state;
    private String error;
    private ReactionResponseTo reaction;
    private List<ReactionResponseTo> reactions;

    // Конструктор без параметров.
    public ReactionKafkaMessage() {
    }

    // Получение идентификатора запроса.
    public String getRequestId() {
        return requestId;
    }

    // Установка идентификатора запроса.
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    // Получение типа операции.
    public String getOperation() {
        return operation;
    }

    // Установка типа операции.
    public void setOperation(String operation) {
        this.operation = operation;
    }

    // Получение идентификатора реакции.
    public Long getReactionId() {
        return reactionId;
    }

    // Установка идентификатора реакции.
    public void setReactionId(Long reactionId) {
        this.reactionId = reactionId;
    }

    // Получение идентификатора темы.
    public Long getTopicId() {
        return topicId;
    }

    // Установка идентификатора темы.
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    // Получение идентификатора.
    public Long getId() {
        return id;
    }

    // Установка идентификатора.
    public void setId(Long id) {
        this.id = id;
    }

    // Получение содержимого реакции.
    public String getContent() {
        return content;
    }

    // Установка содержимого реакции.
    public void setContent(String content) {
        this.content = content;
    }

    // Получение состояния реакции.
    public String getState() {
        return state;
    }

    // Установка состояния реакции.
    public void setState(String state) {
        this.state = state;
    }

    // Получение описания ошибки.
    public String getError() {
        return error;
    }

    // Установка описания ошибки.
    public void setError(String error) {
        this.error = error;
    }

    // Получение одной реакции.
    public ReactionResponseTo getReaction() {
        return reaction;
    }

    // Установка одной реакции.
    public void setReaction(ReactionResponseTo reaction) {
        this.reaction = reaction;
    }

    // Получение списка реакций.
    public List<ReactionResponseTo> getReactions() {
        return reactions;
    }

    // Установка списка реакций.
    public void setReactions(List<ReactionResponseTo> reactions) {
        this.reactions = reactions;
    }
}
