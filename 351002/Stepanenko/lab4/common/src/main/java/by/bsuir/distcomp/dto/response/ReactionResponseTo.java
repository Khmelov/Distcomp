package by.bsuir.distcomp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import by.bsuir.distcomp.dto.reaction.ReactionState;

public class ReactionResponseTo {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("tweetId")
    private Long tweetId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("state")
    private ReactionState state;

    public ReactionResponseTo() {}

    public ReactionResponseTo(Long id, Long tweetId, String content) {
        this(id, tweetId, content, null);
    }

    public ReactionResponseTo(Long id, Long tweetId, String content, ReactionState state) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
        this.state = state;
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

    public ReactionState getState() {
        return state;
    }

    public void setState(ReactionState state) {
        this.state = state;
    }
}
