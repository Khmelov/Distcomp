package com.task.rest.repository;

import com.task.rest.model.Tweet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TweetRepository extends InMemoryRepository<Tweet> {

    @Override
    protected Long getId(Tweet entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Tweet entity, Long id) {
        entity.setId(id);
    }

    public List<Tweet> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(tweet -> tweet.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    public List<Tweet> findByMarkId(Long markId) {
        return storage.values().stream()
                .filter(tweet -> tweet.getMarkIds().contains(markId))
                .collect(Collectors.toList());
    }
}