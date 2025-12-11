package org.example.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

@Table("tbl_comment")
public class Comment {

    @PrimaryKey
    private CommentKey key;

    @Column("content")
    private String content;

    public Comment() {}

    public Comment(CommentKey key, String content) {
        this.key = key;
        this.content = content;
    }

    public CommentKey getKey() { return key; }
    public void setKey(CommentKey key) { this.key = key; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
