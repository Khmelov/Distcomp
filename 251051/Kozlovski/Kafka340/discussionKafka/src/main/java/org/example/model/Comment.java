package org.example.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.Objects;

@Table("tbl_comment")
public class Comment {

    @PrimaryKey
    private CommentKey key;

    @Column("content")
    private String content;

    @Column("state")
    private String state;

    @Column("created")
    private LocalDateTime created;

    public Comment() {}

    public Comment(CommentKey key, String content, String state, LocalDateTime created) {
        this.key = key;
        this.content = content;
        this.state = Objects.requireNonNullElse(state, "PENDING").toUpperCase();
        this.created = Objects.requireNonNullElse(created, LocalDateTime.now());
    }

    public Comment(CommentKey key, String content) {
        this(key, content, "PENDING", LocalDateTime.now());
    }


    public CommentKey getKey() {
        return key;
    }

    public void setKey(CommentKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = Objects.requireNonNullElse(state, "PENDING").toUpperCase();
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = Objects.requireNonNullElse(created, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "key=" + key +
                ", content='" + content + '\'' +
                ", state='" + state + '\'' +
                ", created=" + created +
                '}';
    }
}