package com.example.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("tbl_comment")
public class Comment {
    @PrimaryKeyColumn(name = "story_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long storyId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    private String content;
    private Instant created = Instant.now();

    // --- constructors ---
    public Comment() {}
    public Comment(Long storyId, Long id, String content) {
        this.storyId = storyId;
        this.id = id;
        this.content = content;
    }

    // --- getters & setters ---
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }
}