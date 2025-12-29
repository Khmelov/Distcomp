package com.rest.service;

import com.rest.dto.request.TweetRequestTo;
import com.rest.dto.response.TweetResponseTo;
import com.rest.entity.Tweet;
import com.rest.mapper.TweetMapper;
import com.rest.repository.inmemory.InMemoryTweetRepository;
import com.rest.repository.inmemory.InMemoryWriterRepository;
import com.rest.exception.NotFoundException;
import com.rest.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Service
@Validated
public class TweetService {
    
	@Autowired
    private final InMemoryTweetRepository tweetRepository;
	
	@Autowired
    private final InMemoryWriterRepository writerRepository;
	
	@Autowired
    private final TweetMapper tweetMapper;
    
    public TweetService(InMemoryTweetRepository tweetRepository,
                       InMemoryWriterRepository writerRepository,
                       TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.writerRepository = writerRepository;
        this.tweetMapper = tweetMapper;
    }
    
    public TweetResponseTo create(@Valid TweetRequestTo request) {
        validateTweetRequest(request);
        
        if (!writerRepository.existsById(request.getWriterId())) {
            throw new NotFoundException("Writer not found: " + request.getWriterId());
        }
        
        if (tweetRepository.existsByTitle(request.getTitle())) {
            throw new ValidationException("Tweet title already exists: " + request.getTitle());
        }
        
        Tweet tweet = tweetMapper.toEntity(request);
        Tweet saved = tweetRepository.save(tweet);
        return tweetMapper.toResponse(saved);
    }
    
    public TweetResponseTo findById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        return tweetMapper.toResponse(tweet);
    }
    
    public List<TweetResponseTo> findAll() {
        return tweetRepository.findAll().stream()
            .map(tweetMapper::toResponse)
            .toList();
    }
    
    public TweetResponseTo update(Long id, @Valid TweetRequestTo request) {
        validateTweetRequest(request);
        
        Tweet existing = tweetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        
        if (!writerRepository.existsById(request.getWriterId())) {
            throw new NotFoundException("Writer not found: " + request.getWriterId());
        }
        
        if (!existing.getTitle().equals(request.getTitle()) && 
            tweetRepository.existsByTitle(request.getTitle())) {
            throw new ValidationException("Tweet title already exists: " + request.getTitle());
        }
        
        tweetMapper.updateEntity(request, existing);
        Tweet updated = tweetRepository.update(existing);
        return tweetMapper.toResponse(updated);
    }
    
    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new NotFoundException("Tweet not found: " + id);
        }
        tweetRepository.deleteById(id);
    }
    
    public List<TweetResponseTo> findByWriterId(Long writerId) {
        if (!writerRepository.existsById(writerId)) {
            throw new NotFoundException("Writer not found: " + writerId);
        }
        
        return tweetRepository.findByWriterId(writerId).stream()
            .map(tweetMapper::toResponse)
            .toList();
    }
    
    private void validateTweetRequest(TweetRequestTo request) {
        if (request.getWriterId() == null) {
            throw new ValidationException("Writer ID is required");
        }
        if (request.getTitle() == null || request.getTitle().length() < 2 || request.getTitle().length() > 64) {
            throw new ValidationException("Title must be 2-64 characters");
        }
        if (request.getContent() == null || request.getContent().length() < 4 || request.getContent().length() > 2048) {
            throw new ValidationException("Content must be 4-2048 characters");
        }
    }
}