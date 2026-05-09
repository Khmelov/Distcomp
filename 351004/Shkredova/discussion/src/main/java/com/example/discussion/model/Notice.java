package com.example.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDateTime;

@Table("tbl_notice")
public class Notice {
    @PrimaryKey
    private NoticeKey key;

    @Column("content")
    private String content;

    @Column("created")
    private LocalDateTime created;

    @Column("modified")
    private LocalDateTime modified;

    @Column("state")
    private String state;

    // геттеры/сеттеры (обязательно!)
    public NoticeKey getKey() { return key; }
    public void setKey(NoticeKey key) { this.key = key; }
    public String getCountry() { return key != null ? key.getCountry() : null; }
    public void setCountry(String country) { if (key == null) key = new NoticeKey(); key.setCountry(country); }
    public Long getNewsId() { return key != null ? key.getNewsId() : null; }
    public void setNewsId(Long newsId) { if (key == null) key = new NoticeKey(); key.setNewsId(newsId); }
    public Long getId() { return key != null ? key.getId() : null; }
    public void setId(Long id) { if (key == null) key = new NoticeKey(); key.setId(id); }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}