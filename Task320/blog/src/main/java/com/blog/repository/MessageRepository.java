package com.blog.repository;

import com.blog.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAll(Pageable pageable);

    List<Message> findByTopicId(Long topicId);

    @Query("SELECT m FROM Message m WHERE m.topic.id = :topicId")
    Page<Message> findByTopicId(@Param("topicId") Long topicId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.id = :id")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.topic.id = :topicId")
    boolean existsByTopicId(@Param("topicId") Long topicId);
}