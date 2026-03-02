package by.bsuir.distcomp.entity;

public class Reaction {
    private Long id;
    private Long tweetId;
    private String content;

    public Reaction() {}

    public Reaction(Long id, Long tweetId, String content) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
