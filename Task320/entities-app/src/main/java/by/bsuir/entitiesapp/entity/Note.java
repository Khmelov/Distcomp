package by.bsuir.entitiesapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }
}
