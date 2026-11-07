package com.example.task310.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Модель реакции, связанной с темой.
public class Reaction {

    private Long id;

    private Long topicId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;

    // Конструктор без параметров.
    public Reaction() {
    }

    // Конструктор для создания реакции с заданными значениями.
    public Reaction(Long id, Long topicId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
    }

    // Методы доступа к полям модели.
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