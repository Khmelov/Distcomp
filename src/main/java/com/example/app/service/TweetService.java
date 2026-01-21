package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import com.example.app.dto.TweetRequestDTO;
import com.example.app.dto.TweetResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Author;
import com.example.app.model.Tweet;
import com.example.app.repository.InMemoryAuthorRepository;
import com.example.app.repository.InMemoryTweetRepository;

import java.time.Instant;
import java.util.List;

@Service
public class TweetService {
    private final InMemoryTweetRepository tweetRepo;
    private final InMemoryAuthorRepository authorRepo;

    public TweetService(InMemoryTweetRepository tweetRepo, InMemoryAuthorRepository authorRepo) {
        this.tweetRepo = tweetRepo;
        this.authorRepo = authorRepo;
    }

    public List<TweetResponseDTO> getAllTweets() {
        return tweetRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TweetResponseDTO getTweetById(@NotNull Long id) {
        return tweetRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Tweet not found", 40404));
    }

    public TweetResponseDTO createTweet(@Valid TweetRequestDTO request) {
        Author author = authorRepo.findById(request.authorId())
                .orElseThrow(() -> new AppException("Author not found for tweet", 40401));
        
        Tweet tweet = toEntity(request);
        tweet.setCreated(Instant.now());
        tweet.setModified(Instant.now());
        Tweet saved = tweetRepo.save(tweet);
        return toResponse(saved);
    }

    public TweetResponseDTO updateTweet(@Valid TweetRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!tweetRepo.findById(request.id()).isPresent()) {
            throw new AppException("Tweet not found for update", 40404);
        }
        
        authorRepo.findById(request.authorId())
                .orElseThrow(() -> new AppException("Author not found for tweet update", 40401));
        
        Tweet tweet = toEntity(request);
        tweet.setModified(Instant.now());
        Tweet updated = tweetRepo.save(tweet);
        return toResponse(updated);
    }

    public void deleteTweet(@NotNull Long id) {
        if (!tweetRepo.deleteById(id)) {
            throw new AppException("Tweet not found for deletion", 40404);
        }
    }

    private Tweet toEntity(TweetRequestDTO dto) {
        Tweet tweet = new Tweet();
        tweet.setId(dto.id());
        tweet.setAuthorId(dto.authorId());
        tweet.setTitle(dto.title());
        tweet.setContent(dto.content());
        return tweet;
    }

    private TweetResponseDTO toResponse(Tweet tweet) {
        return new TweetResponseDTO(
                tweet.getId(),
                tweet.getAuthorId(),
                tweet.getTitle(),
                tweet.getContent(),
                tweet.getCreated(),
                tweet.getModified()
        );
    }
}