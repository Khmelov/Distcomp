package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponseTo {
    private String country;
    private Long topicId;
    private Long id;
    private String content;
    private Long editorId;
    private String state;  // PENDING, APPROVED, DECLINED, etc.

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modified;

    // Boolean поля для совместимости
    private Boolean approved;
    private Boolean declined;
    private Boolean pending;

    // Конструкторы
    public MessageResponseTo() {}

    public MessageResponseTo(String country, Long topicId, Long id, String content,
                             Long editorId, String state, LocalDateTime created, LocalDateTime modified) {
        this.country = country;
        this.topicId = topicId;
        this.id = id;
        this.content = content;
        this.editorId = editorId;
        this.state = state;
        this.created = created;
        this.modified = modified;
        updateBooleanFieldsFromState();
    }

    // Метод для обновления boolean полей на основе state
    private void updateBooleanFieldsFromState() {
        if (state != null) {
            this.approved = "APPROVED".equals(state) || "APPROVE".equals(state);
            this.declined = "DECLINED".equals(state) || "DECLINE".equals(state);
            this.pending = "PENDING".equals(state);
        }
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

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        updateBooleanFieldsFromState();
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
        if (approved != null && approved) {
            this.state = "APPROVED";
        }
    }

    public Boolean getDeclined() {
        return declined;
    }

    public void setDeclined(Boolean declined) {
        this.declined = declined;
        if (declined != null && declined) {
            this.state = "DECLINED";
        }
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
        if (pending != null && pending) {
            this.state = "PENDING";
        }
    }

    @Override
    public String toString() {
        return "MessageResponseTo{" +
                "country='" + country + '\'' +
                ", topicId=" + topicId +
                ", id=" + id +
                ", content='" + content + '\'' +
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

