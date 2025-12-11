package org.example.dto;

public class CommentResponseTo {

    private Long id;
    private Long tweetId;
    private String country;
    private String content;

    public CommentResponseTo(Long id, Long tweetId, String country, String content) {
        this.id = id;
        this.tweetId = tweetId;
        this.country = country;
        this.content = content;
    }

    public Long getId() { return id; }
    public Long getTweetId() { return tweetId; }
    public String getCountry() { return country; }
    public String getContent() { return content; }
}