package com.example.task330.publisher.repository;

import com.example.task330.publisher.entity.Editor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Репозиторий для выполнения операций с редакторами в базе данных.
@Repository
public interface EditorRepository extends BaseRepository<Editor, Long> {

    // Поиск редактора по уникальному логину.
    Optional<Editor> findByLogin(String login);
}
