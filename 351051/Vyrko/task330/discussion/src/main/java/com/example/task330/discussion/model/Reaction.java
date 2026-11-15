
package com.example.task330.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

// Модель реакции, хранящейся в Cassandra.
@Table("tbl_reaction_by_id")
public class Reaction {

    @PrimaryKey
    private Long id;

    @Column("topic_id")
    private Long topicId;

    @Column("content")
    private String content;

    @Column("state")
    private String state = "APPROVE";

    // Конструктор без параметров.
    public Reaction() {
    }

    // Конструктор для создания реакции.
    public Reaction(Long id, Long topicId, String content) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
    }

    // Конструктор для создания реакции с состоянием.
    public Reaction(Long id, Long topicId, String content, String state) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.state = state;
    }

    // Получение идентификатора реакции.
    public Long getId() {
        return id;
    }

    // Изменение идентификатора реакции.
    public void setId(Long id) {
        this.id = id;
    }

    // Получение идентификатора темы.
    public Long getTopicId() {
        return topicId;
    }

    // Изменение идентификатора темы.
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    // Получение содержимого реакции.
    public String getContent() {
        return content;
    }

    // Изменение содержимого реакции.
    public void setContent(String content) {
        this.content = content;
    }

    // Получение состояния реакции.
    public String getState() {
        return state == null ? "APPROVE" : state;
    }

    // Изменение состояния реакции.
    public void setState(String state) {
        this.state = state;
    }
}
