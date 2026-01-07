package com.task310.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class PostKey implements Serializable {
    @PrimaryKeyColumn(name = "article_id", type = PrimaryKeyType.PARTITIONED)
    private Long articleId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public PostKey() {
    }

    public PostKey(Long articleId, Long id) {
        this.articleId = articleId;
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostKey postKey = (PostKey) o;
        return Objects.equals(articleId, postKey.articleId) && Objects.equals(id, postKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, id);
    }
}

