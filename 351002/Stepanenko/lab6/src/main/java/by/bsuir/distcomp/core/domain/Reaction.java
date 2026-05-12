package by.bsuir.distcomp.core.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_reaction")
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(nullable = false, length = 2048)
    private String content;

    public Reaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tweet getTweet() { return tweet; }
    public void setTweet(Tweet tweet) { this.tweet = tweet; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}