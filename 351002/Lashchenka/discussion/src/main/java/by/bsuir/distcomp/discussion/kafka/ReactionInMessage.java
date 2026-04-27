package by.bsuir.distcomp.discussion.kafka;

public class ReactionInMessage {

    private String correlationId;
    private String operation;
    private Long tweetId;
    private Long reactionId;
    private ReactionSnapshot snapshot;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Long getReactionId() {
        return reactionId;
    }

    public void setReactionId(Long reactionId) {
        this.reactionId = reactionId;
    }

    public ReactionSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(ReactionSnapshot snapshot) {
        this.snapshot = snapshot;
    }
}
