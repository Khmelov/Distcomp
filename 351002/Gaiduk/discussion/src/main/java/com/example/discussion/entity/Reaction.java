package com.example.discussion.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Objects;

@Table("tbl_reaction")
public class Reaction {

    @PrimaryKey
    private ReactionKey key;

    private String content;

    public Reaction() {
    }

    public Reaction(ReactionKey key, String content) {
        this.key = key;
        this.content = content;
    }

    public ReactionKey getKey() {
        return key;
    }

    public void setKey(ReactionKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Удобные методы – они используют key, который не может быть null
    public Long getTweetId() {
        return key != null ? key.getTweetId() : null;
    }

    public Long getId() {
        return key != null ? key.getId() : null;
    }

    public void setTweetId(Long tweetId) {
        if (key == null) key = new ReactionKey();
        key.setTweetId(tweetId);
    }

    public void setId(Long id) {
        if (key == null) key = new ReactionKey();
        key.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reaction reaction)) return false;
        return Objects.equals(key, reaction.key) && Objects.equals(content, reaction.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, content);
    }
}