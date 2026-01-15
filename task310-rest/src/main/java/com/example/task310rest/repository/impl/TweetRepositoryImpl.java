package com.example.task310rest.repository.impl;

import com.example.task310rest.entity.Tweet;
import com.example.task310rest.repository.TweetRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InMemory реализация репозитория для Tweet
 */
@Repository
public class TweetRepositoryImpl extends InMemoryCrudRepository<Tweet> implements TweetRepository {
    
    @Override
    protected Long getId(Tweet entity) {
        return entity.getId();
    }
    
    @Override
    protected void setId(Tweet entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public Tweet save(Tweet entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return super.save(entity);
    }
    
    @Override
    public Tweet update(Tweet entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(entity);
    }
    
    @Override
    public List<Tweet> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(tweet -> tweet.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Tweet> findByTitleContaining(String title) {
        return storage.values().stream()
                .filter(tweet -> tweet.getTitle() != null && 
                        tweet.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Tweet> findByContentContaining(String content) {
        return storage.values().stream()
                .filter(tweet -> tweet.getContent() != null && 
                        tweet.getContent().toLowerCase().contains(content.toLowerCase()))
                .collect(Collectors.toList());
    }
}
