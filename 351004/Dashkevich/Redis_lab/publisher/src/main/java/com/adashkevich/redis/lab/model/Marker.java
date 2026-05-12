package com.adashkevich.redis.lab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_marker")
public class Marker extends BaseEntity {
    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @ManyToMany(mappedBy = "markers")
    private Set<News> news = new HashSet<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<News> getNews() { return news; }
    public void setNews(Set<News> news) { this.news = news; }
}
