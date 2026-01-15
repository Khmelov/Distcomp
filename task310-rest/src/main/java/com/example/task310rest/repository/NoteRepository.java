package com.example.task310rest.repository;

import com.example.task310rest.entity.Note;

import java.util.List;

/**
 * Репозиторий для сущности Note
 * Расширяет базовый CrudRepository дополнительными методами поиска
 */
public interface NoteRepository extends CrudRepository<Note, Long> {
    
    /**
     * Найти все заметки для твита
     * @param tweetId ID твита
     * @return список заметок
     */
    List<Note> findByTweetId(Long tweetId);
}
