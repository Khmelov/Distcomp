package com.example.task310.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_mark")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    private String name;

    @ManyToMany(mappedBy = "marks")
    private List<News> news = new ArrayList<>();

    // Конструкторы
    public Mark() {}

    public Mark(Long id, String name, List<News> news) {
        this.id = id;
        this.name = name;
        this.news = news;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<News> getNews() { return news; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setNews(List<News> news) { this.news = news; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return id != null && id.equals(mark.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}