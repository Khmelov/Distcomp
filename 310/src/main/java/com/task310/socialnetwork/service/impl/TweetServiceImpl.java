package com.task310.socialnetwork.service.impl;

import com.task310.socialnetwork.dto.request.TweetRequestTo;
import com.task310.socialnetwork.dto.response.TweetResponseTo;
import com.task310.socialnetwork.mapper.TweetMapper;
import com.task310.socialnetwork.model.Tweet;
import com.task310.socialnetwork.repository.TweetRepository;
import com.task310.socialnetwork.service.TweetService;
import com.task310.socialnetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetMapper tweetMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TweetResponseTo getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + id));
        return tweetMapper.toResponse(tweet);
    }

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        if (!userService.existsById(request.getUserId())) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }

        Tweet tweet = tweetMapper.toEntity(request);
        Tweet saved = tweetRepository.save(tweet);
        return tweetMapper.toResponse(saved);
    }

    @Override
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        if (!tweetRepository.existsById(id)) {
            throw new RuntimeException("Tweet not found with id: " + id);
        }

        if (!userService.existsById(request.getUserId())) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }

        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setId(id);
        Tweet updated = tweetRepository.update(tweet);
        return tweetMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!tweetRepository.deleteById(id)) {
            throw new RuntimeException("Tweet not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return tweetRepository.existsById(id);
    }

    @Override
    public List<TweetResponseTo> getByUserId(Long userId) {
        return tweetRepository.findByUserId(userId).stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TweetResponseTo> getByLabelId(Long labelId) {
        return tweetRepository.findByLabelIdsContaining(labelId).stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }
}