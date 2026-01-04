package com.blog.repository;

import com.blog.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAll(Pageable pageable);

    List<Message> findByTopicId(Long topicId);

    // ИСПРАВИТЬ: использовать topicId вместо topic.id
    @Query("SELECT m FROM Message m WHERE m.topicId = :topicId")
    Page<Message> findByTopicId(@Param("topicId") Long topicId, Pageable pageable);

    // Новый метод: найти сообщения по editorId
    List<Message> findByEditorId(Long editorId);

    // Новый метод: найти сообщения по editorId с пагинацией
    @Query("SELECT m FROM Message m WHERE m.editorId = :editorId")
    Page<Message> findByEditorId(@Param("editorId") Long editorId, Pageable pageable);

    // Новый метод: проверить существует ли сообщение от определенного редактора
    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.id = :id AND m.editorId = :editorId")
    boolean existsByIdAndEditorId(@Param("id") Long id, @Param("editorId") Long editorId);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.id = :id")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.topicId = :topicId")
    boolean existsByTopicId(@Param("topicId") Long topicId);

    // Новый метод: найти сообщение по ID и editorId (для проверки владельца)
    @Query("SELECT m FROM Message m WHERE m.id = :id AND m.editorId = :editorId")
    Optional<Message> findByIdAndEditorId(@Param("id") Long id, @Param("editorId") Long editorId);

    // Добавить этот метод для Kafka consumer
    Optional<Message> findById(Long id);

    @Query("SELECT MAX(m.id) FROM Message m")
    Optional<Long> findMaxId();
}