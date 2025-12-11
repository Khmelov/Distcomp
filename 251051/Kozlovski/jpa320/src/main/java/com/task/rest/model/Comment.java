package com.task.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_comment")
public class Comment extends BaseEntity {
    @NotNull(message = "Tweet ID cannot be null")
    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be at most 2048 characters")
    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    public Comment() {}

    public Comment(Long tweetId, String content) {
        this.tweetId = tweetId;
        this.content = content;
    }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
