package com.blog.discussion.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponseTo {
    private String country;
    private Long topicId;
    private Long id;
    private String content;
    private Long editorId;
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modified;

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

    // Вспомогательные методы
    public boolean isPending() {
        return "PENDING".equals(state) || (pending != null && pending);
    }

    public boolean isApproved() {
        return "APPROVED".equals(state) || "APPROVE".equals(state) || (approved != null && approved);
    }

    public boolean isDeclined() {
        return "DECLINED".equals(state) || "DECLINE".equals(state) || (declined != null && declined);
    }

    private void updateBooleanFieldsFromState() {
        if (state != null) {
            this.approved = isApproved();
            this.declined = isDeclined();
            this.pending = isPending();
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

    public void setAllBooleanFields() {
        if (state != null) {
            this.approved = "APPROVED".equals(this.state) || "APPROVE".equals(this.state);
            this.declined = "DECLINED".equals(this.state) || "DECLINE".equals(this.state);
            this.pending = "PENDING".equals(this.state);
        } else {
            this.approved = false;
            this.declined = false;
            this.pending = false;
        }
    }

    @Override
    public String toString() {
        return "MessageResponseTo{" +
                "id=" + id +
                ", topicId=" + topicId +
                ", content='" + content + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", editorId=" + editorId +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}