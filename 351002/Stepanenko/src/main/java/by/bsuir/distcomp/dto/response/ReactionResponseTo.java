package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReactionResponseTo {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("tweetId")
    private Long tweetId;

    @JsonProperty("content")
    private String content;

    public ReactionResponseTo() {}

    public ReactionResponseTo(Long id, Long tweetId, String content) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
