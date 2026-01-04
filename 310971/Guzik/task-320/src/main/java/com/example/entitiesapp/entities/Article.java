package com.example.entitiesapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_article")
@Getter
@Setter
public class Article extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Writer writer;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "article_sticker",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "sticker_id")
    )
    private Set<Sticker> stickers = new HashSet<>();
}