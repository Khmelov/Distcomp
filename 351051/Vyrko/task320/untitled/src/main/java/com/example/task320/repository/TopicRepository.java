package com.example.task320.repository;

import com.example.task320.entity.Topic;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

// Репозиторий для выполнения операций с темами в базе данных.
@Repository
public interface TopicRepository extends BaseRepository<Topic, Long> {

    // Поиск всех тем определённого редактора.
    List<Topic> findByEditor_Id(Long editorId);
    Optional<Topic> findByTitle(String title);
    // Проверка, используется ли маркер хотя бы одной темой.
    boolean existsByMarkers_Id(Long markerId);
}