
package com.example.task330.discussion.repository;

import com.example.task330.discussion.model.ReactionByTopic;
import com.example.task330.discussion.model.ReactionByTopicKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

// Репозиторий обеспечивает поиск реакций по идентификатору темы.
public interface ReactionByTopicRepository
        extends CassandraRepository<ReactionByTopic, ReactionByTopicKey> {

    // Получение всех реакций определённой темы.
    List<ReactionByTopic> findByKeyTopicId(Long topicId);

    // Удаление всех реакций определённой темы.
    void deleteByKeyTopicId(Long topicId);
}
