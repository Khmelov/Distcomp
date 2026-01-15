package com.example.task310rest.repository.impl;

import com.example.task310rest.entity.Note;
import com.example.task310rest.repository.NoteRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InMemory реализация репозитория для Note
 */
@Repository
public class NoteRepositoryImpl extends InMemoryCrudRepository<Note> implements NoteRepository {
    
    @Override
    protected Long getId(Note entity) {
        return entity.getId();
    }
    
    @Override
    protected void setId(Note entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public Note save(Note entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return super.save(entity);
    }
    
    @Override
    public Note update(Note entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(entity);
    }
    
    @Override
    public List<Note> findByTweetId(Long tweetId) {
        return storage.values().stream()
                .filter(note -> note.getTweetId().equals(tweetId))
                .collect(Collectors.toList());
    }
}
