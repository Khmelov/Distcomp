package com.example.discussion.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Table("tbl_post_by_story")
public class PostByStory {
    @PrimaryKey
    private PostByStoryKey key;

    private String content;

    @Column("created_at")
    private Instant createdAt;

    @Column("modified_at")
    private Instant modifiedAt;
}
