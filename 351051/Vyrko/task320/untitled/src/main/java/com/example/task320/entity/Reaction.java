package com.example.task320.entity;

import jakarta.persistence.*;

// Сущность реакции, соответствующая таблице tbl_reaction в базе данных
@Entity
@Table(name = "tbl_reaction")
public class Reaction {

    // Уникальный идентификатор реакции, генерируемый базой данных
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Тема, к которой относится реакция
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // Текстовое содержимое реакции
    @Column(columnDefinition = "TEXT")
    private String content;

    // Конструктор без параметров, необходимый для JPA
    public Reaction() {
    }

    // Конструктор для создания реакции с указанием темы и содержимого
    public Reaction(Topic topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    // Возвращает идентификатор реакции
    public Long getId() {
        return id;
    }

    // Устанавливает идентификатор реакции
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает тему, к которой относится реакция
    public Topic getTopic() {
        return topic;
    }

    // Устанавливает тему реакции
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    // Возвращает содержимое реакции
    public String getContent() {
        return content;
    }

    // Устанавливает содержимое реакции
    public void setContent(String content) {
        this.content = content;
    }
}
