package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_tag", schema = "distcomp")
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 32)
    @Size(min = 2, max = 32)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Topic> topics = new HashSet<>();

    // Конструкторы
    public Tag() {
        super();
    }

    public Tag(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }
}