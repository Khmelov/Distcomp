package com.example.task320.entity;

import jakarta.persistence.*;
import java.util.List;

// Сущность маркера, соответствующая таблице tbl_marker в базе данных
@Entity
@Table(name = "tbl_marker")
public class Marker {

    // Уникальный идентификатор маркера, генерируемый базой данных
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название маркера
    @Column(nullable = false, unique = true)
    private String name;

    // Список тем, к которым относится маркер
    @ManyToMany(mappedBy = "markers")
    private List<Topic> topics;

    // Конструктор без параметров, необходимый для JPA
    public Marker() {
    }

    // Конструктор для создания маркера по названию
    public Marker(String name) {
        this.name = name;
    }

    // Возвращает идентификатор маркера
    public Long getId() {
        return id;
    }

    // Устанавливает идентификатор маркера
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает название маркера
    public String getName() {
        return name;
    }

    // Устанавливает название маркера
    public void setName(String name) {
        this.name = name;
    }

    // Возвращает список тем, связанных с маркером
    public List<Topic> getTopics() {
        return topics;
    }

    // Устанавливает список связанных тем
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
