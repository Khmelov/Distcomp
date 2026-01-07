package com.task310.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("tbl_post")
public class Post {
    @PrimaryKey
    private PostKey key;
    
    private String content;
    
    @Column("state")
    private PostState state;
    
    private LocalDateTime created;
    private LocalDateTime modified;

    public Post() {
    }

    public Post(PostKey key, String content, PostState state, LocalDateTime created, LocalDateTime modified) {
        this.key = key;
        this.content = content;
        this.state = state;
        this.created = created;
        this.modified = modified;
    }

    public PostKey getKey() {
        return key;
    }

    public void setKey(PostKey key) {
        this.key = key;
    }

    public Long getId() {
        return key != null ? key.getId() : null;
    }

    public void setId(Long id) {
        if (key == null) {
            key = new PostKey();
        }
        key.setId(id);
    }

    public Long getArticleId() {
        return key != null ? key.getArticleId() : null;
    }

    public void setArticleId(Long articleId) {
        if (key == null) {
            key = new PostKey();
        }
        key.setArticleId(articleId);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
    }
}

