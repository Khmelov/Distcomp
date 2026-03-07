package com.example.task310.service.impl;

import com.example.task310.domain.dto.request.TweetRequestTo;
import com.example.task310.domain.dto.response.TweetResponseTo;
import com.example.task310.domain.entity.Tweet;
import com.example.task310.exception.EntityNotFoundException;
import com.example.task310.mapper.TweetMapper;
import com.example.task310.repository.TweetRepository;
import com.example.task310.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        Tweet tweet = tweetMapper.toEntity(request);
        // Бизнес-логика: установка меток времени
        tweet.setCreated(LocalDateTime.now());
        tweet.setModified(LocalDateTime.now());
        return tweetMapper.toResponse(tweetRepository.save(tweet));
    }

    @Override
    public List<TweetResponseTo> findAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TweetResponseTo findById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found"));
        return tweetMapper.toResponse(tweet);
    }

    @Override
    public TweetResponseTo update(TweetRequestTo request) {
        Tweet existing = tweetRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found"));
        
        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setCreated(existing.getCreated()); // Сохраняем дату создания
        tweet.setModified(LocalDateTime.now());   // Обновляем дату модификации
        
        return tweetMapper.toResponse(tweetRepository.save(tweet));
    }

    @Override
    public void deleteById(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new EntityNotFoundException("Tweet not found with id: " + id);
        }
        tweetRepository.deleteById(id);
    }
}