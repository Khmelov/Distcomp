package by.bsuir.distcomp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_tweet")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "editor_id", nullable = false)
    private Long editorId;

    @Column(name = "title", nullable = false, unique = true, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime modified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_tweet_mark",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private List<Mark> marks = new ArrayList<>();

    public Tweet() {}

    public Tweet(Long id, Long editorId, String title, String content, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.editorId = editorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public List<Mark> getMarks() { return marks; }
    public void setMarks(List<Mark> marks) { this.marks = marks; }
}
