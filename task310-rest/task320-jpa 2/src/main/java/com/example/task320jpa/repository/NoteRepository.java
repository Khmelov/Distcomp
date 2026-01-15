package com.example.task320jpa.repository;

import com.example.task320jpa.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository для сущности Note
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    /**
     * Найти все заметки для твита
     */
    List<Note> findByTweetId(Long tweetId);
    
    /**
     * Найти все заметки для твита с пагинацией
     */
    Page<Note> findByTweetId(Long tweetId, Pageable pageable);
    
    /**
     * Найти все заметки с пагинацией
     */
    Page<Note> findAll(Pageable pageable);
}
