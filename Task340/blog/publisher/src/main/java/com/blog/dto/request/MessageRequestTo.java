package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageRequestTo {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;


    private Long editorId;


    @Size(min = 2, max = 10, message = "Country must be between 2 and 10 characters")
    private String country;

    private Long id;  // ID может быть сгенерирован на стороне publisher

    private String state;  // PENDING, APPROVE, DECLINE (для ответов от discussion)

    // Конструкторы
    public MessageRequestTo() {
        this.state = "PENDING";  // Значение по умолчанию при создании
    }

    public MessageRequestTo(Long topicId, String content, Long editorId, String country) {
        this();
        this.topicId = topicId;
        this.content = content;
        this.editorId = editorId;
        this.country = country;
    }

    public MessageRequestTo(Long topicId, String content, Long editorId, String country, String state) {
        this.topicId = topicId;
        this.content = content;
        this.editorId = editorId;
        this.country = country;
        this.state = state;
    }

    // Геттеры и сеттеры
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

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "MessageRequestTo{" +
                "topicId=" + topicId +
                ", content='" + content + '\'' +
                ", editorId=" + editorId +
                ", country='" + country + '\'' +
                ", id=" + id +
                ", state='" + state + '\'' +
                '}';
    }
}

