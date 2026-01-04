package com.example.entitiesapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_post")
@Getter
@Setter
public class Post extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;
}