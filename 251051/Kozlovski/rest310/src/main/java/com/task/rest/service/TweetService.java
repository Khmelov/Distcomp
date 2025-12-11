package com.task.rest.service;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.model.Tweet;
import com.task.rest.repository.InMemoryRepository;
import com.task.rest.util.TweetMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private final InMemoryRepository<Tweet> tweetRepository;
    private final TweetMapper tweetMapper;

    public TweetService(InMemoryRepository<Tweet> tweetRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.tweetMapper = tweetMapper;
    }

    public TweetResponseTo createTweet(@Valid TweetRequestTo requestTo) {
        Tweet tweet = tweetMapper.toEntity(requestTo);
        Tweet savedTweet = tweetRepository.save(tweet);
        return tweetMapper.toResponse(savedTweet);
    }

    public TweetResponseTo getTweetById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + id));
        return tweetMapper.toResponse(tweet);
    }

    public List<TweetResponseTo> getAllTweets() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public TweetResponseTo updateTweet(Long id, @Valid TweetRequestTo requestTo) {
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + id));
        tweetMapper.updateEntityFromDto(requestTo, existingTweet);
        Tweet updatedTweet = tweetRepository.save(existingTweet);
        return tweetMapper.toResponse(updatedTweet);
    }

    public void deleteTweet(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new RuntimeException("Tweet not found with id: " + id);
        }
        tweetRepository.deleteById(id);
    }

    public List<TweetResponseTo> getTweetsByMarkId(Long markId) {
        return tweetRepository.findByMarkId(markId).stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TweetResponseTo> getTweetsByWriterId(Long writerId) {
        return tweetRepository.findByWriterId(writerId).stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TweetResponseTo> getTweetsByMarkName(String markName) {
        return tweetRepository.findAll().stream()
                .filter(tweet -> tweet.getMarks().stream()
                        .anyMatch(mark -> mark.getName().equals(markName)))
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }
}
