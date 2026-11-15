
package com.example.task330.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Данные реакции, которые приходят от клиента.
public class ReactionRequestTo {

    @NotNull
    private Long topicId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;

    // Конструктор без параметров.
    public ReactionRequestTo() {
    }

    // Конструктор для заполнения данных реакции.
    public ReactionRequestTo(Long topicId, String content) {
        this.topicId = topicId;
        this.content = content;
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
}
