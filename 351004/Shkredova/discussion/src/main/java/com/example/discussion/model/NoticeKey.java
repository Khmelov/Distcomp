package com.example.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class NoticeKey implements Serializable {
    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "news_id", type = PrimaryKeyType.CLUSTERED)
    private Long newsId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public NoticeKey() {}

    public NoticeKey(String country, Long newsId, Long id) {
        this.country = country;
        this.newsId = newsId;
        this.id = id;
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeKey that = (NoticeKey) o;
        return Objects.equals(country, that.country) &&
                Objects.equals(newsId, that.newsId) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, newsId, id);
    }
}