package com.example.app.service;

import com.example.app.dto.TweetRequestDTO;
import com.example.app.dto.TweetResponseDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.dto.cache.CachedTweetDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Tweet;
import com.example.app.repository.AuthorRepository;
import com.example.app.repository.TweetRepository;
import com.example.app.client.DiscussionClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TweetService {
    private final TweetRepository tweetRepo;
    private final AuthorRepository authorRepo;
    private final DiscussionClient discussionClient;
    private final RedisCacheService cacheService;
    
    public TweetService(TweetRepository tweetRepo, 
                       AuthorRepository authorRepo,
                       DiscussionClient discussionClient,
                       RedisCacheService cacheService) {
        this.tweetRepo = tweetRepo;
        this.authorRepo = authorRepo;
        this.discussionClient = discussionClient;
        this.cacheService = cacheService;
    }
    
    @Cacheable(value = "tweets")
    public List<TweetResponseDTO> getAllTweets() {
        return tweetRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Cacheable(value = "tweets", key = "#id")
    public TweetResponseDTO getTweetById(@NotNull Long id) {
        // Проверяем кеш
        CachedTweetDTO cached = cacheService.getCachedTweet(id);
        if (cached != null) {
            return convertFromCache(cached);
        }
        
        Tweet tweet = tweetRepo.findById(id)
                .orElseThrow(() -> new AppException("Tweet not found", 40404));
        
        // Получаем реакции
        List<ReactionResponseDTO> reactions = discussionClient.getReactionsByTweetId(id);
        
        TweetResponseDTO response = toResponse(tweet).withReactions(reactions);
        
        // Кешируем
        CachedTweetDTO cacheDto = convertToCache(tweet, reactions);
        cacheService.cacheTweet(id, cacheDto);
        
        return response;
    }
    
    @Cacheable(value = "tweets", key = "#id + '_approved'")
    public TweetResponseDTO getTweetWithApprovedReactions(@NotNull Long id) {
        CachedTweetDTO cached = cacheService.getCachedTweet(id);
        if (cached != null && cached.getReactions() != null) {
            // Фильтруем только APPROVED реакции из кеша
            List<ReactionResponseDTO> approvedReactions = cached.getReactions().stream()
                .filter(r -> "APPROVE".equals(r.getState()))
                .toList();
            
            return new TweetResponseDTO(
                cached.getId(),
                cached.getAuthorId(),
                cached.getTitle(),
                cached.getContent(),
                cached.getCreated(),
                cached.getModified(),
                approvedReactions
            );
        }
        
        Tweet tweet = tweetRepo.findById(id)
                .orElseThrow(() -> new AppException("Tweet not found", 40404));
        
        List<ReactionResponseDTO> approvedReactions = discussionClient.getApprovedReactionsByTweetId(id);
        
        TweetResponseDTO response = toResponse(tweet).withReactions(approvedReactions);
        
        return response;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "tweets", key = "#request.id()"),
        @CacheEvict(value = "tweets", allEntries = true)
    })
    public TweetResponseDTO createTweet(@Valid TweetRequestDTO request) {
        if (!authorRepo.existsById(request.authorId())) {
            throw new AppException("Author not found for tweet", 40401);
        }
        
        Tweet tweet = toEntity(request);
        tweet.setCreated(Instant.now());
        tweet.setModified(Instant.now());
        Tweet saved = tweetRepo.save(tweet);
        
        // Очищаем кеш
        cacheService.evictAllTweetsCache();
        
        return toResponse(saved);
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "tweets", key = "#request.id()"),
        @CacheEvict(value = "tweets", allEntries = true)
    })
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
        
        // Инвалидируем кеш
        cacheService.evictTweetCache(request.id());
        
        return toResponse(updated);
    }
    
    @Transactional
    @CacheEvict(value = "tweets", key = "#id")
    public void deleteTweet(@NotNull Long id) {
        if (!tweetRepo.existsById(id)) {
            throw new AppException("Tweet not found for deletion", 40404);
        }
        
        // Удаляем реакции
        discussionClient.deleteReactionsByTweetId(id);
        
        tweetRepo.deleteById(id);
        
        // Очищаем кеш
        cacheService.evictTweetCache(id);
        cacheService.evictTweetReactionsCache(id);
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
    
    private CachedTweetDTO convertToCache(Tweet tweet, List<ReactionResponseDTO> reactions) {
        return new CachedTweetDTO(
            tweet.getId(),
            tweet.getAuthorId(),
            tweet.getTitle(),
            tweet.getContent(),
            tweet.getCreated(),
            tweet.getModified(),
            reactions,
            List.of() // tagIds можно добавить позже
        );
    }
    
    private TweetResponseDTO convertFromCache(CachedTweetDTO cached) {
        return new TweetResponseDTO(
            cached.getId(),
            cached.getAuthorId(),
            cached.getTitle(),
            cached.getContent(),
            cached.getCreated(),
            cached.getModified(),
            cached.getReactions()
        );
    }
}