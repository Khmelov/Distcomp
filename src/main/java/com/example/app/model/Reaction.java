package com.example.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_reaction")
public class Reaction extends BaseEntity {
    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;
    
    @NotBlank @Size(min = 2, max = 2048)
    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    //конструктор
    public Reaction() {}
    public Reaction(Long id, Long tweetId, String content) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
    }

    //геттеры и сеттеры
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}