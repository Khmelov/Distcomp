package com.example.discussion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_article", schema = "distcomp")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Author author;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_article_sticker",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "sticker_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Sticker> stickers = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Note> notes = new ArrayList<>();
}