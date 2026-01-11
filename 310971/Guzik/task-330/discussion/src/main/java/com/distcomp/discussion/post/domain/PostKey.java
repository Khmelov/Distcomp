package com.distcomp.discussion.post.domain;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class PostKey implements Serializable {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "article_id", type = PrimaryKeyType.CLUSTERED, ordinal = 0)
    private long articleId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    private Long id;

    public PostKey() {
    }

    public PostKey(String country, long articleId, Long id) {
        this.country = country;
        this.articleId = articleId;
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostKey postKey = (PostKey) o;
        return articleId == postKey.articleId && Objects.equals(country, postKey.country) && Objects.equals(id, postKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, articleId, id);
    }
}
