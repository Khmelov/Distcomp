#!/bin/bash

# Создаем оставшиеся сервисы и контроллеры на основе UserService/UserController

# TweetService
cat > src/main/java/com/example/task320jpa/service/TweetService.java << 'EOF'
package com.example.task320jpa.service;

import com.example.task320jpa.dto.request.TweetRequestTo;
import com.example.task320jpa.dto.response.TweetResponseTo;
import com.example.task320jpa.entity.Tweet;
import com.example.task320jpa.exception.ResourceNotFoundException;
import com.example.task320jpa.exception.ValidationException;
import com.example.task320jpa.mapper.TweetMapper;
import com.example.task320jpa.repository.TweetRepository;
import com.example.task320jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TweetService {
    
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    
    public TweetResponseTo create(TweetRequestTo requestTo) {
        if (!userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        Tweet tweet = tweetMapper.toEntity(requestTo);
        Tweet savedTweet = tweetRepository.save(tweet);
        return tweetMapper.toResponseTo(savedTweet);
    }
    
    @Transactional(readOnly = true)
    public TweetResponseTo getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        return tweetMapper.toResponseTo(tweet);
    }
    
    @Transactional(readOnly = true)
    public Page<TweetResponseTo> getAll(Pageable pageable) {
        return tweetRepository.findAll(pageable)
                .map(tweetMapper::toResponseTo);
    }
    
    public TweetResponseTo update(Long id, TweetRequestTo requestTo) {
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        
        if (!userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        
        existingTweet.setUserId(requestTo.getUserId());
        existingTweet.setTitle(requestTo.getTitle());
        existingTweet.setContent(requestTo.getContent());
        
        Tweet updatedTweet = tweetRepository.save(existingTweet);
        return tweetMapper.toResponseTo(updatedTweet);
    }
    
    public TweetResponseTo partialUpdate(Long id, TweetRequestTo requestTo) {
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        
        if (requestTo.getUserId() != null && !userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        
        tweetMapper.updateEntityFromRequestTo(requestTo, existingTweet);
        Tweet updatedTweet = tweetRepository.save(existingTweet);
        return tweetMapper.toResponseTo(updatedTweet);
    }
    
    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet", id);
        }
        tweetRepository.deleteById(id);
    }
}
EOF

echo "TweetService created"
