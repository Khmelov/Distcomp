package com.example.task320.repository;

import com.example.task320.entity.Reaction;
import org.springframework.stereotype.Repository;

import java.util.List;

// Репозиторий для выполнения операций с реакциями в базе данных.
@Repository
public interface ReactionRepository extends BaseRepository<Reaction, Long> {

    // Поиск всех реакций определённой темы.
    List<Reaction> findByTopic_Id(Long topicId);
}