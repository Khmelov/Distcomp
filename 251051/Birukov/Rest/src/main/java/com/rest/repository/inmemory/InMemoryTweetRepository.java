package com.rest.repository.inmemory;

import com.rest.entity.Tweet;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTweetRepository {
    
    private final Map<Long, Tweet> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Tweet save(Tweet tweet) {
		if (tweet == null) {
			throw new IllegalArgumentException("Tweet cannot be null");
		}
		
        if (tweet.getId() == null) {
            tweet.setId(idGenerator.getAndIncrement());
        }
        tweet.setModified(java.time.LocalDateTime.now());
        storage.put(tweet.getId(), tweet);
        return tweet;
    }
	
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    public List<Tweet> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    public Tweet update(Tweet tweet) {
        if (!storage.containsKey(tweet.getId())) {
            throw new RuntimeException("Tweet not found with id: " + tweet.getId());
        }
        tweet.setModified(java.time.LocalDateTime.now());
        storage.put(tweet.getId(), tweet);
        return tweet;
    }
    
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
	
	public List<Tweet> findByWriterId(Long writerId) {
        return storage.values().stream()
            .filter(tweet -> tweet.getWriterId().equals(writerId))
            .toList();
    }
    
    public Optional<Tweet> findByTitle(String title) {
        return storage.values().stream()
            .filter(tweet -> tweet.getTitle().equals(title))
            .findFirst();
    }
    
    public boolean existsByTitle(String title) {
        return storage.values().stream()
            .anyMatch(tweet -> tweet.getTitle().equals(title));
    }
}