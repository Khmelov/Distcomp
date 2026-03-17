package com.example.restApi.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_article")
public class Article extends BaseEntity {

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", length = 2048)
    private String content;

    // Связь: Одна статья принадлежит одному пользователю (Много статей -> Один пользователь)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Связь: У одной статьи может быть много комментариев
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    // Связь: Многие-ко-многим с маркерами
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_article_marker",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Marker> markers = new HashSet<>();

    public Article() {
        super();
    }

    // Геттеры и сеттеры

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(Set<Marker> markers) {
        this.markers = markers;
    }
}
