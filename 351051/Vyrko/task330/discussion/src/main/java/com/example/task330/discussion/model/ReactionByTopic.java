
package com.example.task330.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

// Модель реакции для получения списка реакций по теме.
@Table("tbl_reaction_by_topic")
public class ReactionByTopic {

    @PrimaryKey
    private ReactionByTopicKey key;

    @Column("content")
    private String content;

    @Column("state")
    private String state = "APPROVE";

    // Конструктор без параметров.
    public ReactionByTopic() {
    }

    // Конструктор для создания записи реакции по теме.
    public ReactionByTopic(ReactionByTopicKey key, String content) {
        this.key = key;
        this.content = content;
    }

    // Конструктор для создания записи реакции с состоянием.
    public ReactionByTopic(
            ReactionByTopicKey key,
            String content,
            String state) {
        this.key = key;
        this.content = content;
        this.state = state;
    }

    // Получение составного ключа реакции.
    public ReactionByTopicKey getKey() {
        return key;
    }

    // Изменение составного ключа реакции.
    public void setKey(ReactionByTopicKey key) {
        this.key = key;
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
