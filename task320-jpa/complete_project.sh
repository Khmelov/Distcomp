#!/bin/bash

# Создаем MarkService
cat > src/main/java/com/example/task320jpa/service/MarkService.java << 'MARK_SERVICE'
package com.example.task320jpa.service;

import com.example.task320jpa.dto.request.MarkRequestTo;
import com.example.task320jpa.dto.response.MarkResponseTo;
import com.example.task320jpa.entity.Mark;
import com.example.task320jpa.exception.ResourceNotFoundException;
import com.example.task320jpa.mapper.MarkMapper;
import com.example.task320jpa.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkService {
    private final MarkRepository markRepository;
    private final MarkMapper markMapper;
    
    public MarkResponseTo create(MarkRequestTo requestTo) {
        Mark mark = markMapper.toEntity(requestTo);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponseTo(savedMark);
    }
    
    @Transactional(readOnly = true)
    public MarkResponseTo getById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        return markMapper.toResponseTo(mark);
    }
    
    @Transactional(readOnly = true)
    public Page<MarkResponseTo> getAll(Pageable pageable) {
        return markRepository.findAll(pageable).map(markMapper::toResponseTo);
    }
    
    public MarkResponseTo update(Long id, MarkRequestTo requestTo) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark", id));
        existingMark.setName(requestTo.getName());
        Mark updatedMark = markRepository.save(existingMark);
        return markMapper.toResponseTo(updatedMark);
    }
    
    public void delete(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark", id);
        }
        markRepository.deleteById(id);
    }
}
MARK_SERVICE

# Создаем NoteService
cat > src/main/java/com/example/task320jpa/service/NoteService.java << 'NOTE_SERVICE'
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
NOTE_SERVICE

echo "Services created successfully"
