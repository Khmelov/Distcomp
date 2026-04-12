package by.bsuir.task330.discussion.entity;

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

    public Reaction() {
    }

    public Reaction(Long id, Long tweetId, String content) {
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
