package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
@Entity
@Table(name = "tbl_tag", schema = "distcomp")
@Profile("docker")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 2, max = 32)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<News> news = new ArrayList<>();

    public Tag() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<News> getNews() { return news; }
    public void setNews(List<News> news) { this.news = news; }
}