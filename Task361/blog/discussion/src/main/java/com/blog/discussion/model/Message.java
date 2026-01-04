package com.blog.discussion.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Table("tbl_message")
public class Message {

    @PrimaryKeyColumn(
            name = "country",
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 0
    )
    private String country;

    @PrimaryKeyColumn(
            name = "topic_id",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.ASCENDING,
            ordinal = 1
    )
    private Long topicId;

    @PrimaryKeyColumn(
            name = "id",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.ASCENDING,
            ordinal = 2
    )
    private Long id;

    @Column("content")
    private String content;

    @Column("editor_id")  // ПРАВИЛЬНОЕ ИМЯ КОЛОНКИ
    private Long editorId;

    @Column("state")
    private String state;

    @Column("created")
    private LocalDateTime created;

    @Column("modified")
    private LocalDateTime modified;

    // Конструкторы
    public Message() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.state = "PENDING";
    }

    public Message(String country, Long topicId, Long id, String content, Long editorId, String state) {
        this();
        this.country = country;
        this.topicId = topicId;
        this.id = id;
        this.content = content;
        this.editorId = editorId;
        this.state = state;
    }

    // Геттеры и сеттеры
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        this.modified = LocalDateTime.now();
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
        this.modified = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.modified = LocalDateTime.now();
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
        this.modified = LocalDateTime.now();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(country, message.country) &&
                Objects.equals(topicId, message.topicId) &&
                Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, topicId, id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "country='" + country + '\'' +
                ", topicId=" + topicId +
                ", id=" + id +
                ", content='" + content + '\'' +
                ", editorId=" + editorId +
                ", state='" + state + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}

