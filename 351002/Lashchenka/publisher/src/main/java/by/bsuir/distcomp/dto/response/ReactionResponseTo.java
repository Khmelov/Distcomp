package by.bsuir.distcomp.dto.response;

import by.bsuir.distcomp.model.ReactionState;

public class ReactionResponseTo {
    private Long id;
    private Long tweetId;
    private String content;
    private ReactionState state;

    public ReactionResponseTo() {}

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
