package com.blog.discussion.repository;

import com.blog.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Long> {

    List<Message> findByCountry(String country);

    // Находим все сообщения по country и topic_id
    List<Message> findByCountryAndTopicId(String country, Long topicId);

    // Находим сообщения по country и topic_id с пагинацией
    @Query("SELECT * FROM tbl_message WHERE country = ?0 AND topic_id = ?1")
    Slice<Message> findByCountryAndTopicId(String country, Long topicId, Pageable pageable);

    // Находим сообщение по country, topic_id и id
    Message findByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    // Удаляем сообщение по country, topic_id и id
    void deleteByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    // Проверяем существование сообщения
    boolean existsByCountryAndTopicIdAndId(String country, Long topicId, Long id);

    @Query("SELECT * FROM tbl_message")
    List<Message> findAllMessages();
}