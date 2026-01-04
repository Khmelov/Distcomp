package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaMessageDto {
    private Long id;
    private Long topicId;
    private String content;
    private Long editorId;
    private String country;
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modified;

    // Конструкторы
    public KafkaMessageDto() {}

    public KafkaMessageDto(Long id, Long topicId, String content, String state) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.state = state;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}