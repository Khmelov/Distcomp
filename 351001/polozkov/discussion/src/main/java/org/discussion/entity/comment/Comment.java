package org.discussion.entity.comment;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Table("tbl_comment")
public class Comment {

    @PrimaryKey
    private CommentKey key;

    @Column("author_id")
    private Long authorId;

    @Column("content")
    private String content;

    @Column("created_at")
    private Instant createdAt;
}
