package com.example.task310rest.service;

import com.example.task310rest.dto.request.NoteRequestTo;
import com.example.task310rest.dto.response.NoteResponseTo;
import com.example.task310rest.entity.Note;
import com.example.task310rest.exception.ResourceNotFoundException;
import com.example.task310rest.exception.ValidationException;
import com.example.task310rest.mapper.NoteMapper;
import com.example.task310rest.repository.NoteRepository;
import com.example.task310rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Note
 */
@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final TweetRepository tweetRepository;
    private final NoteMapper noteMapper;
    
    /**
     * Создать новую заметку
     */
    public NoteResponseTo create(NoteRequestTo requestTo) {
        // Проверяем существование твита
        if (!tweetRepository.existsById(requestTo.getTweetId())) {
            throw new ValidationException("Tweet with id=" + requestTo.getTweetId() + " not found");
        }
        
        Note note = noteMapper.toEntity(requestTo);
        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponseTo(savedNote);
    }
    
    /**
     * Получить заметку по ID
     */
    public NoteResponseTo getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        return noteMapper.toResponseTo(note);
    }
    
    /**
     * Получить все заметки
     */
    public List<NoteResponseTo> getAll() {
        return noteRepository.findAll().stream()
                .map(noteMapper::toResponseTo)
                .collect(Collectors.toList());
    }
    
    /**
     * Получить заметки по ID твита
     */
    public List<NoteResponseTo> getByTweetId(Long tweetId) {
        return noteRepository.findByTweetId(tweetId).stream()
                .map(noteMapper::toResponseTo)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновить заметку
     */
    public NoteResponseTo update(Long id, NoteRequestTo requestTo) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        
        // Проверяем существование твита
        if (!tweetRepository.existsById(requestTo.getTweetId())) {
            throw new ValidationException("Tweet with id=" + requestTo.getTweetId() + " not found");
        }
        
        existingNote.setTweetId(requestTo.getTweetId());
        existingNote.setContent(requestTo.getContent());
        
        Note updatedNote = noteRepository.update(existingNote);
        return noteMapper.toResponseTo(updatedNote);
    }
    
    /**
     * Частичное обновление заметки (PATCH)
     */
    public NoteResponseTo partialUpdate(Long id, NoteRequestTo requestTo) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        
        // Если обновляется tweetId, проверяем существование твита
        if (requestTo.getTweetId() != null && !tweetRepository.existsById(requestTo.getTweetId())) {
            throw new ValidationException("Tweet with id=" + requestTo.getTweetId() + " not found");
        }
        
        noteMapper.updateEntityFromRequestTo(requestTo, existingNote);
        Note updatedNote = noteRepository.update(existingNote);
        return noteMapper.toResponseTo(updatedNote);
    }
    
    /**
     * Удалить заметку
     */
    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note", id);
        }
        noteRepository.deleteById(id);
    }
}
