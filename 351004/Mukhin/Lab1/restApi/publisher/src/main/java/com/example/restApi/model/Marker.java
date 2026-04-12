package com.example.restApi.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_marker")
public class Marker extends BaseEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @ManyToMany(mappedBy = "markers", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();

    public Marker() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }
}
