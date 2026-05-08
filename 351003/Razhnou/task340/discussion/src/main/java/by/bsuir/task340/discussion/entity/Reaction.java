package by.bsuir.task340.discussion.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_reaction")
public class Reaction {
    @Id
    private Long id;

    @Column("tweet_id")
    private Long tweetId;

    @Column("content")
    private String content;

    @Column("state")
    private String state;

    public Reaction() {
    }

    public Reaction(Long id, Long tweetId, String content, String state) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
