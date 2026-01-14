package com.blog.discussion.repository;

import com.blog.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Long> {

    List<Message> findByCountry(String country);

    List<Message> findByCountryAndTopicId(String country, Long topicId);

    @Query("SELECT * FROM tbl_message WHERE country = ?0 AND topic_id = ?1")
    Slice<Message> findByCountryAndTopicId(String country, Long topicId, Pageable pageable);

    @Query("SELECT * FROM tbl_message WHERE country = ?0 AND topic_id = ?1 AND id = ?2")
    Message findByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    @Query("DELETE FROM tbl_message WHERE country = ?0 AND topic_id = ?1 AND id = ?2")
    void deleteByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    @Query("SELECT COUNT(*) > 0 FROM tbl_message WHERE country = ?0 AND topic_id = ?1 AND id = ?2")
    boolean existsByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    @Query("SELECT * FROM tbl_message")
    List<Message> findAllMessages();

    // Новый метод для поиска по ID (без country и topicId)
    @Query("SELECT * FROM tbl_message WHERE id = ?0 ALLOW FILTERING")
    List<Message> findByIdAllowFiltering(Long id);

    // Метод для поиска любого сообщения с указанным ID
    default Optional<Message> findAnyById(Long id) {
        List<Message> messages = findByIdAllowFiltering(id);
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }
}

