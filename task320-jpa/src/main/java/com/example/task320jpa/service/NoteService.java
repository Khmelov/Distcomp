package com.example.task320jpa.service;

import com.example.task320jpa.dto.request.NoteRequestTo;
import com.example.task320jpa.dto.response.NoteResponseTo;
import com.example.task320jpa.entity.Note;
import com.example.task320jpa.exception.ResourceNotFoundException;
import com.example.task320jpa.exception.ValidationException;
import com.example.task320jpa.mapper.NoteMapper;
import com.example.task320jpa.repository.NoteRepository;
import com.example.task320jpa.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    private final TweetRepository tweetRepository;
    private final NoteMapper noteMapper;
    
    public NoteResponseTo create(NoteRequestTo requestTo) {
        if (!tweetRepository.existsById(requestTo.getTweetId())) {
            throw new ValidationException("Tweet with id=" + requestTo.getTweetId() + " not found");
        }
        Note note = noteMapper.toEntity(requestTo);
        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponseTo(savedNote);
    }
    
    @Transactional(readOnly = true)
    public NoteResponseTo getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        return noteMapper.toResponseTo(note);
    }
    
    @Transactional(readOnly = true)
    public Page<NoteResponseTo> getAll(Pageable pageable) {
        return noteRepository.findAll(pageable).map(noteMapper::toResponseTo);
    }
    
    public NoteResponseTo update(Long id, NoteRequestTo requestTo) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        if (!tweetRepository.existsById(requestTo.getTweetId())) {
            throw new ValidationException("Tweet with id=" + requestTo.getTweetId() + " not found");
        }
        existingNote.setTweetId(requestTo.getTweetId());
        existingNote.setContent(requestTo.getContent());
        Note updatedNote = noteRepository.save(existingNote);
        return noteMapper.toResponseTo(updatedNote);
    }
    
    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note", id);
        }
        noteRepository.deleteById(id);
    }
}
