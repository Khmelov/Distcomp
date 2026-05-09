package by.bsuir.distcomp.discussion.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Таблица-поиск по id: внешний API использует только id, без tweet_id.
 */
@Table("tbl_reaction_by_id")
public class ReactionById {

    @PrimaryKey
    private Long id;

    @Column("tweet_id")
    private Long tweetId;

    @Column("content")
    private String content;

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
