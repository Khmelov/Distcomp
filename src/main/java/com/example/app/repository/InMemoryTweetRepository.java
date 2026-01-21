package com.example.app.repository;

import org.springframework.stereotype.Repository;

import com.example.app.model.Tweet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTweetRepository implements CrudRepository<Tweet, Long> {
    private final Map<Long, Tweet> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Tweet> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Tweet save(Tweet tweet) {
        if (tweet.getId() == null) {
            tweet.setId(idGenerator.getAndIncrement());
        }
        store.put(tweet.getId(), tweet);
        return tweet;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public List<Tweet> findByAuthorId(Long authorId) {
        return store.values().stream()
                .filter(tweet -> authorId.equals(tweet.getAuthorId()))
                .collect(Collectors.toList());
    }
}