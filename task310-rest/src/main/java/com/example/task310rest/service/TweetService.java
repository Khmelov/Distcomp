package com.example.task310rest.service;

import com.example.task310rest.dto.request.TweetRequestTo;
import com.example.task310rest.dto.response.MarkResponseTo;
import com.example.task310rest.dto.response.NoteResponseTo;
import com.example.task310rest.dto.response.TweetResponseTo;
import com.example.task310rest.dto.response.UserResponseTo;
import com.example.task310rest.entity.Tweet;
import com.example.task310rest.exception.ResourceNotFoundException;
import com.example.task310rest.exception.ValidationException;
import com.example.task310rest.mapper.TweetMapper;
import com.example.task310rest.repository.TweetMarkRepository;
import com.example.task310rest.repository.TweetRepository;
import com.example.task310rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Tweet
 */
@Service
@RequiredArgsConstructor
public class TweetService {
    
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMarkRepository tweetMarkRepository;
    private final TweetMapper tweetMapper;
    private final UserService userService;
    private final MarkService markService;
    private final NoteService noteService;
    
    /**
     * Создать новый твит
     */
    public TweetResponseTo create(TweetRequestTo requestTo) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        
        Tweet tweet = tweetMapper.toEntity(requestTo);
        Tweet savedTweet = tweetRepository.save(tweet);
        return tweetMapper.toResponseTo(savedTweet);
    }
    
    /**
     * Получить твит по ID
     */
    public TweetResponseTo getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        return tweetMapper.toResponseTo(tweet);
    }
    
    /**
     * Получить все твиты
     */
    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponseTo)
                .collect(Collectors.toList());
    }
    
    /**
     * Обновить твит
     */
    public TweetResponseTo update(Long id, TweetRequestTo requestTo) {
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        
        // Проверяем существование пользователя
        if (!userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        
        existingTweet.setUserId(requestTo.getUserId());
        existingTweet.setTitle(requestTo.getTitle());
        existingTweet.setContent(requestTo.getContent());
        
        Tweet updatedTweet = tweetRepository.update(existingTweet);
        return tweetMapper.toResponseTo(updatedTweet);
    }
    
    /**
     * Частичное обновление твита (PATCH)
     */
    public TweetResponseTo partialUpdate(Long id, TweetRequestTo requestTo) {
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", id));
        
        // Если обновляется userId, проверяем существование пользователя
        if (requestTo.getUserId() != null && !userRepository.existsById(requestTo.getUserId())) {
            throw new ValidationException("User with id=" + requestTo.getUserId() + " not found");
        }
        
        tweetMapper.updateEntityFromRequestTo(requestTo, existingTweet);
        Tweet updatedTweet = tweetRepository.update(existingTweet);
        return tweetMapper.toResponseTo(updatedTweet);
    }
    
    /**
     * Удалить твит
     */
    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet", id);
        }
        
        // Удаляем связи с метками
        tweetMarkRepository.removeAllByTweetId(id);
        
        tweetRepository.deleteById(id);
    }
    
    /**
     * Получить пользователя по ID твита
     */
    public UserResponseTo getUserByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet", tweetId));
        return userService.getById(tweet.getUserId());
    }
    
    /**
     * Получить метки по ID твита
     */
    public List<MarkResponseTo> getMarksByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException("Tweet", tweetId);
        }
        
        List<Long> markIds = tweetMarkRepository.getMarkIdsByTweetId(tweetId);
        return markIds.stream()
                .map(markService::getById)
                .collect(Collectors.toList());
    }
    
    /**
     * Получить заметки по ID твита
     */
    public List<NoteResponseTo> getNotesByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException("Tweet", tweetId);
        }
        return noteService.getByTweetId(tweetId);
    }
    
    /**
     * Получить твиты по ID метки
     */
    public List<TweetResponseTo> getTweetsByMarkId(Long markId) {
        List<Long> tweetIds = tweetMarkRepository.getTweetIdsByMarkId(markId);
        return tweetIds.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
    
    /**
     * Получить твиты по имени метки
     */
    public List<TweetResponseTo> getTweetsByMarkName(String markName) {
        // Находим метку по имени
        var mark = markService.getByName(markName);
        return getTweetsByMarkId(mark.getId());
    }
    
    /**
     * Получить твиты с фильтрацией
     */
    public List<TweetResponseTo> getTweetsWithFilters(
            List<Long> markIds, 
            String markName, 
            String userLogin, 
            String title, 
            String content) {
        
        List<TweetResponseTo> result = getAll();
        
        // Фильтр по markIds
        if (markIds != null && !markIds.isEmpty()) {
            result = result.stream()
                    .filter(tweet -> {
                        List<Long> tweetMarkIds = tweetMarkRepository.getMarkIdsByTweetId(tweet.getId());
                        return markIds.stream().anyMatch(tweetMarkIds::contains);
                    })
                    .collect(Collectors.toList());
        }
        
        // Фильтр по markName
        if (markName != null && !markName.isEmpty()) {
            var mark = markService.getByName(markName);
            List<Long> tweetIdsForMark = tweetMarkRepository.getTweetIdsByMarkId(mark.getId());
            result = result.stream()
                    .filter(tweet -> tweetIdsForMark.contains(tweet.getId()))
                    .collect(Collectors.toList());
        }
        
        // Фильтр по userLogin
        if (userLogin != null && !userLogin.isEmpty()) {
            result = result.stream()
                    .filter(tweet -> {
                        UserResponseTo user = userService.getById(tweet.getUserId());
                        return user.getLogin().equalsIgnoreCase(userLogin);
                    })
                    .collect(Collectors.toList());
        }
        
        // Фильтр по title
        if (title != null && !title.isEmpty()) {
            result = result.stream()
                    .filter(tweet -> tweet.getTitle() != null && 
                            tweet.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Фильтр по content
        if (content != null && !content.isEmpty()) {
            result = result.stream()
                    .filter(tweet -> tweet.getContent() != null && 
                            tweet.getContent().toLowerCase().contains(content.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return result;
    }
}
