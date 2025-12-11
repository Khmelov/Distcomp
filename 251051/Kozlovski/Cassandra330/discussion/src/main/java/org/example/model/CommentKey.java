package org.example.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class CommentKey implements Serializable {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "tweet_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    private Long tweetId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public CommentKey() {}

    public CommentKey(String country, Long tweetId, Long id) {
        this.country = country;
        this.tweetId = tweetId;
        this.id = id;
    }

    public String getCountry() { return country; }
    public Long getTweetId() { return tweetId; }
    public Long getId() { return id; }

    public void setCountry(String country) { this.country = country; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public void setId(Long id) { this.id = id; }
}