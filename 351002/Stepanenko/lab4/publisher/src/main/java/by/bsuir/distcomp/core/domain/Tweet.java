package by.bsuir.distcomp.core.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_tweet")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;

    @ManyToMany
    @JoinTable(name = "tbl_tweet_marker",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id"))
    private Set<Marker> markers = new HashSet<>();

    public Tweet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public Set<Marker> getMarkers() { return markers; }
    public void setMarkers(Set<Marker> markers) { this.markers = markers; }
}