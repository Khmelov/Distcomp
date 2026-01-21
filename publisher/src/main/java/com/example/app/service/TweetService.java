package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dto.TweetRequestDTO;
import com.example.app.dto.TweetResponseDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Tweet;
import com.example.app.repository.AuthorRepository;
import com.example.app.repository.TweetRepository;
import com.example.app.client.DiscussionClient;

import java.time.Instant;
import java.util.List;

@Service
public class TweetService {
    private final TweetRepository tweetRepo;
    private final AuthorRepository authorRepo;
    private final DiscussionClient discussionClient;

    // Добавляем DiscussionClient в конструктор
    public TweetService(TweetRepository tweetRepo, 
                       AuthorRepository authorRepo,
                       DiscussionClient discussionClient) {
        this.tweetRepo = tweetRepo;
        this.authorRepo = authorRepo;
        this.discussionClient = discussionClient;
    }

    public List<TweetResponseDTO> getAllTweets() {
        return tweetRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TweetResponseDTO getTweetById(@NotNull Long id) {
        Tweet tweet = tweetRepo.findById(id)
                .orElseThrow(() -> new AppException("Tweet not found", 40404));
        
        // Получаем реакции из discussion модуля
        List<ReactionResponseDTO> reactions = discussionClient.getReactionsByTweetId(id);
        
        TweetResponseDTO response = toResponse(tweet);
        // Если у TweetResponseDTO есть поле для реакций, добавляем их
        response.setReactions(reactions);
        
        return response;
    }

    @Transactional
    public TweetResponseDTO createTweet(@Valid TweetRequestDTO request) {
        if (!authorRepo.existsById(request.authorId())) {
            throw new AppException("Author not found for tweet", 40401);
        }
        
        Tweet tweet = toEntity(request);
        tweet.setCreated(Instant.now());
        tweet.setModified(Instant.now());
        Tweet saved = tweetRepo.save(tweet);
        
        return toResponse(saved);
        // Реакции создаются отдельно через discussion модуль
    }

    @Transactional
    public TweetResponseDTO updateTweet(@Valid TweetRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        
        Tweet existingTweet = tweetRepo.findById(request.id())
                .orElseThrow(() -> new AppException("Tweet not found for update", 40404));
        
        if (!authorRepo.existsById(request.authorId())) {
            throw new AppException("Author not found for tweet update", 40401);
        }
        
        existingTweet.setAuthorId(request.authorId());
        existingTweet.setTitle(request.title());
        existingTweet.setContent(request.content());
        existingTweet.setModified(Instant.now());
        
        Tweet updated = tweetRepo.save(existingTweet);
        
        return toResponse(updated);
    }

    @Transactional
    public void deleteTweet(@NotNull Long id) {
        if (!tweetRepo.existsById(id)) {
            throw new AppException("Tweet not found for deletion", 40404);
        }
        
        // Удаляем все реакции для этого твита через discussion модуль
        discussionClient.deleteReactionsByTweetId(id);
        
        // Удаляем сам твит
        tweetRepo.deleteById(id);
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
        TweetResponseDTO response = new TweetResponseDTO(
                tweet.getId(),
                tweet.getAuthorId(),
                tweet.getTitle(),
                tweet.getContent(),
                tweet.getCreated(),
                tweet.getModified()
        );
        
        // Не загружаем реакции здесь, чтобы избежать N+1 проблем
        // Реакции загружаются только когда нужно (в getTweetById)
        
        return response;
    }
    
    // Дополнительный метод для получения твита с реакциями
    public TweetResponseDTO getTweetWithReactions(@NotNull Long id) {
        Tweet tweet = tweetRepo.findById(id)
                .orElseThrow(() -> new AppException("Tweet not found", 40404));
        
        List<ReactionResponseDTO> reactions = discussionClient.getReactionsByTweetId(id);
        
        TweetResponseDTO response = toResponse(tweet);
        response.setReactions(reactions);
        
        return response;
    }
}