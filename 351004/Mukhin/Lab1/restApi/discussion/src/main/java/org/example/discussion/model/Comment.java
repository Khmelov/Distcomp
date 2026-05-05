package org.example.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.LocalDateTime;

@Table("tbl_comment")
public class Comment {

    @PrimaryKeyColumn(name = "article_id", type = PrimaryKeyType.PARTITIONED)
    private Long articleId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    @Column("content")
    private String content;

    @Column("state")
    private String state;

    @Column("created")
    private LocalDateTime created;

    @Column("modified")
    private LocalDateTime modified;

    public Comment() {
        this.id = System.currentTimeMillis();
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.state = CommentState.PENDING.name();

    }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}