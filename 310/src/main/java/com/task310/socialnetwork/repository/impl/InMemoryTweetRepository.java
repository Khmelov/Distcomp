package com.task310.socialnetwork.repository.impl;

import com.task310.socialnetwork.model.Tweet;
import com.task310.socialnetwork.repository.TweetRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTweetRepository implements TweetRepository {
    private final Map<Long, Tweet> tweets = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Tweet> findAll() {
        return new ArrayList<>(tweets.values());
    }

    @Override
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(tweets.get(id));
    }

    @Override
    public Tweet save(Tweet tweet) {
        if (tweet.getId() == null) {
            tweet.setId(idCounter.getAndIncrement());
            tweet.setCreated(LocalDateTime.now());
            tweet.setModified(LocalDateTime.now());
        }
        tweets.put(tweet.getId(), tweet);
        return tweet;
    }

    @Override
    public Tweet update(Tweet tweet) {
        if (tweet.getId() == null || !tweets.containsKey(tweet.getId())) {
            throw new IllegalArgumentException("Tweet not found");
        }
        Tweet existing = tweets.get(tweet.getId());
        tweet.setCreated(existing.getCreated());
        tweet.setModified(LocalDateTime.now());
        tweets.put(tweet.getId(), tweet);
        return tweet;
    }

    @Override
    public boolean deleteById(Long id) {
        return tweets.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return tweets.containsKey(id);
    }

    @Override
    public List<Tweet> findByUserId(Long userId) {
        return tweets.values().stream()
                .filter(tweet -> tweet.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tweet> findByLabelIdsContaining(Long labelId) {
        return tweets.values().stream()
                .filter(tweet -> tweet.getLabelIds() != null && tweet.getLabelIds().contains(labelId))
                .collect(Collectors.toList());
    }
}