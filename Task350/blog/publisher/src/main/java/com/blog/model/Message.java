package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_message", schema = "distcomp")
public class Message {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "content", nullable = false, length = 2048)
    @Size(min = 4, max = 2048)
    private String content;

    @Column(name = "editor_id")
    private Long editorId;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "state", length = 20)
    private String state = "PENDING"; // PENDING, APPROVED, DECLINED, DELETED

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "modified")
    private LocalDateTime modified = LocalDateTime.now();

    // Конструкторы
    public Message() {}

    public Message(Long topicId, String content) {
        this.topicId = topicId;
        this.content = content;
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
        this.modified = LocalDateTime.now();
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
        this.modified = LocalDateTime.now();
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

    @PreUpdate
    public void preUpdate() {
        this.modified = LocalDateTime.now();
    }

    // Вспомогательные методы
    public boolean isPending() {
        return "PENDING".equals(state);
    }

    public boolean isApproved() {
        return "APPROVED".equals(state) || "APPROVE".equals(state);
    }

    public boolean isDeclined() {
        return "DECLINED".equals(state) || "DECLINE".equals(state);
    }

    public boolean isDeleted() {
        return "DELETED".equals(state);
    }
}