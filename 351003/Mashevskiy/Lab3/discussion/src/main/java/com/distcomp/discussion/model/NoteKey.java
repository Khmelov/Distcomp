package com.distcomp.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import java.io.Serializable;

@PrimaryKeyClass
public class NoteKey implements Serializable {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country = "default";

    @PrimaryKeyColumn(name = "tweet_id", type = PrimaryKeyType.CLUSTERED)
    private Long tweetId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public NoteKey() {}

    public NoteKey(Long tweetId, Long id) {
        this.tweetId = tweetId;
        this.id = id;
        this.country = "default";
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteKey noteKey = (NoteKey) o;
        return country.equals(noteKey.country) && tweetId.equals(noteKey.tweetId) && id.equals(noteKey.id);
    }

    @Override
    public int hashCode() {
        return 31 * country.hashCode() + 31 * tweetId.hashCode() + id.hashCode();
    }
}