package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_post", schema = "distcomp")
public class Post extends BaseEntity{
    @ManyToOne(optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(nullable = false, length = 2048)
    private String content;

    // Геттеры
    public Story getStory() {
        return story;
    }

    public String getContent() {
        return content;
    }

    // Сеттеры
    public void setStory(Story story) {
        this.story = story;
    }

    public void setContent(String content) {
        this.content = content;
    }
//    Writer → Story	один ко многим
//    Story → Post	один ко многим
//    Story ↔ Tag	многие ко многим
}

//Необходимо реализовать хранение сущностей в реляционной базе данных Postgres
//инициализации базы данных, рекомендуется использовать liquibase в формате XML

//обобщенный интерфейс для хранения и поиска данных сущностей для CRUD операций:
//операции поиска и выборки должны поддерживать пагинацию, фильтрацию и сортировку
