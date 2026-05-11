package com.adashkevich.nosql.lab.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class MessageKey implements Serializable {
    @PrimaryKeyColumn(name = "country", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "news_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long newsId;

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public MessageKey() { }

    public MessageKey(String country, Long newsId, Long id) {
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageKey that)) return false;
        return Objects.equals(country, that.country) && Objects.equals(newsId, that.newsId) && Objects.equals(id, that.id);
    }

    @Override public int hashCode() {
        return Objects.hash(country, newsId, id);
    }
}
