package com.rest.service;

import com.rest.dto.request.NoteRequestTo;
import com.rest.dto.response.NoteResponseTo;
import com.rest.entity.Note;
import com.rest.mapper.NoteMapper;
import com.rest.repository.inmemory.InMemoryNoteRepository;
import com.rest.repository.inmemory.InMemoryTweetRepository;
import com.rest.exception.NotFoundException;
import com.rest.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
public class NoteService {
    
	@Autowired
    private final InMemoryNoteRepository NoteRepository;
	
	@Autowired
    private final InMemoryTweetRepository TweetRepository;
	
	@Autowired
    private final NoteMapper NoteMapper;
    
    public NoteService(InMemoryNoteRepository NoteRepository,
                       InMemoryTweetRepository TweetRepository,
                       NoteMapper NoteMapper) {
        this.NoteRepository = NoteRepository;
        this.TweetRepository = TweetRepository;
        this.NoteMapper = NoteMapper;
    }
    
    public NoteResponseTo create(@Valid NoteRequestTo request) {
        validateNoteRequest(request);
        
        if (!TweetRepository.existsById(request.getTweetId())) {
            throw new NotFoundException("Tweet not found: " + request.getTweetId());
        }
        
        Note Note = NoteMapper.toEntity(request);
        Note saved = NoteRepository.save(Note);
        return NoteMapper.toResponse(saved);
    }
    
    public NoteResponseTo findById(Long id) {
        Note Note = NoteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Note not found: " + id));
        return NoteMapper.toResponse(Note);
    }
    
    public List<NoteResponseTo> findAll() {
        return NoteRepository.findAll().stream()
            .map(NoteMapper::toResponse)
            .toList();
    }
    
    public NoteResponseTo update(Long id, @Valid NoteRequestTo request) {
        validateNoteRequest(request);
        
        Note existing = NoteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Note not found: " + id));
        
        if (!TweetRepository.existsById(request.getTweetId())) {
            throw new NotFoundException("Tweet not found: " + request.getTweetId());
        }
        
        NoteMapper.updateEntity(request, existing);
        Note updated = NoteRepository.update(existing);
        return NoteMapper.toResponse(updated);
    }
    
    public void delete(Long id) {
        if (!NoteRepository.existsById(id)) {
            throw new NotFoundException("Note not found: " + id);
        }
        NoteRepository.deleteById(id);
    }
    
    public List<NoteResponseTo> findByTweetId(Long TweetId) {
        if (!TweetRepository.existsById(TweetId)) {
            throw new NotFoundException("Tweet not found: " + TweetId);
        }
        
        return NoteRepository.findByTweetId(TweetId).stream()
            .map(NoteMapper::toResponse)
            .toList();
    }
    
    private void validateNoteRequest(NoteRequestTo request) {
        if (request.getTweetId() == null) {
            throw new ValidationException("Tweet ID is required");
        }
        if (request.getContent() == null || request.getContent().length() < 2 || request.getContent().length() > 2048) {
            throw new ValidationException("Content must be 2-2048 characters");
        }
    }
}