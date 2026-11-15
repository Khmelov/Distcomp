package com.example.task330.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

// Составной ключ для поиска реакций по идентификатору темы.
@PrimaryKeyClass
public class ReactionByTopicKey {

    @PrimaryKeyColumn(
            name = "topic_id",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED
    )
    private Long topicId;

    @PrimaryKeyColumn(
            name = "id",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED
    )
    private Long id;

    // Конструктор без параметров.
    public ReactionByTopicKey() {
    }

    // Конструктор для заполнения составного ключа.
    public ReactionByTopicKey(Long topicId, Long id) {
        this.topicId = topicId;
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

    // Получение идентификатора реакции.
    public Long getId() {
        return id;
    }

    // Изменение идентификатора реакции.
    public void setId(Long id) {
        this.id = id;
    }
}
