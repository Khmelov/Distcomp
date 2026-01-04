package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponseFromDiscussion {

    private Long id;
    private String content;
    private String country;

    @JsonProperty("topicId")
    private Long topicId;

    @JsonProperty("editorId")
    private Long editorId;

    private String state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modified;

    // Добавляем boolean поля для совместимости
    private Boolean approved;
    private Boolean declined;
    private Boolean pending;

    // Геттеры и сеттеры
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

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public String getState() {
        // Если state не установлен, но есть boolean поля, вычисляем его
        if (state == null) {
            return getNormalizedState();
        }
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getDeclined() {
        return declined;
    }

    public void setDeclined(Boolean declined) {
        this.declined = declined;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    // Метод для получения нормализованного состояния
    public String getNormalizedState() {
        if (state != null && !state.isEmpty()) {
            return state;
        }
        // Если state null, но есть boolean флаги, определяем состояние по ним
        if (approved != null && approved) return "APPROVED";
        if (declined != null && declined) return "DECLINED";
        if (pending != null && pending) return "PENDING";
        return "PENDING"; // По умолчанию
    }

    @Override
    public String toString() {
        return "MessageResponseFromDiscussion{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", country='" + country + '\'' +
                ", topicId=" + topicId +
                ", editorId=" + editorId +
                ", state='" + state + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", approved=" + approved +
                ", declined=" + declined +
                ", pending=" + pending +
                '}';
    }
}

