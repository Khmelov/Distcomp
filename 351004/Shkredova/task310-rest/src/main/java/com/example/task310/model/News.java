package com.example.task310.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tbl_news_mark",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private List<Mark> marks = new ArrayList<>();

    // Геттеры
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreated() { return created; }
    public LocalDateTime getModified() { return modified; }
    public Creator getCreator() { return creator; }
    public List<Mark> getMarks() { return marks; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public void setCreator(Creator creator) { this.creator = creator; }
    public void setMarks(List<Mark> marks) { this.marks = marks; }
}