package com.rest.repository.inmemory;

import com.rest.entity.Note;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryNoteRepository{
    
    private final Map<Long, Note> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Note save(Note note) {
		if (note == null) {
			throw new IllegalArgumentException("Note cannot be null");
		}
		
        if (note.getId() == null) {
            note.setId(idGenerator.getAndIncrement());
        }
        storage.put(note.getId(), note);
        return note;
    }
    
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    public List<Note> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    public Note update(Note note) {
        if (!storage.containsKey(note.getId())) {
            throw new RuntimeException("Note not found with id: " + note.getId());
        }
        storage.put(note.getId(), note);
        return note;
    }
    
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
	
	public List<Note> findByTweetId(Long tweetId) {
        return storage.values().stream()
            .filter(note -> note.getTweetId().equals(tweetId))
            .toList();
    }
}