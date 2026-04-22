package by.bsuir.distcomp.discussion.cassandra;

import by.bsuir.distcomp.discussion.model.ReactionState;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_reaction")
public class ReactionRow {

    @PrimaryKey
    private Long id;

    @Column("tweet_id")
    private Long tweetId;

    @Column("content")
    private String content;

    @Column("state")
    private ReactionState state;

    public ReactionRow() {}

    public ReactionRow(Long id, Long tweetId, String content, ReactionState state) {
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
