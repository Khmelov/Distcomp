package com.socialnetwork.service.impl;

import com.socialnetwork.dto.request.TweetRequestTo;
import com.socialnetwork.dto.response.TweetResponseTo;
import com.socialnetwork.exception.DuplicateResourceException;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.mapper.TweetMapper;
import com.socialnetwork.model.Tweet;
import com.socialnetwork.model.User;
import com.socialnetwork.repository.TweetRepository;
import com.socialnetwork.repository.UserRepository;
import com.socialnetwork.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetMapper tweetMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TweetResponseTo> getAll(Pageable pageable) {
        return tweetRepository.findAll(pageable)
                .map(tweetMapper::toResponse);
    }

    @Override
    public TweetResponseTo getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));
        return tweetMapper.toResponse(tweet);
    }

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (tweetRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Tweet with title '" + request.getTitle() + "' already exists");
        }

        Tweet tweet = tweetMapper.toEntity(request);
        tweet.setUser(user);

        Tweet savedTweet = tweetRepository.save(tweet);
        return tweetMapper.toResponse(savedTweet);
    }

    @Override
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));

        if (!tweet.getUser().getId().equals(request.getUserId())) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            tweet.setUser(user);
        }

        tweet.setTitle(request.getTitle());
        tweet.setContent(request.getContent());

        Tweet updatedTweet = tweetRepository.save(tweet);
        return tweetMapper.toResponse(updatedTweet);
    }

    @Override
    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet not found with id: " + id);
        }
        tweetRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return tweetRepository.existsById(id);
    }

    @Override
    public List<TweetResponseTo> getByUserId(Long userId) {
        List<Tweet> tweets = tweetRepository.findByUserId(userId);
        return tweets.stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TweetResponseTo> getByLabelId(Long labelId) {
        List<Tweet> tweets = tweetRepository.findByLabelId(labelId);
        return tweets.stream()
                .map(tweetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TweetResponseTo> getByUserId(Long userId, Pageable pageable) {
        Page<Tweet> tweets = tweetRepository.findByUserId(userId, pageable);
        return tweets.map(tweetMapper::toResponse);
    }

    @Override
    public Page<TweetResponseTo> getByLabelId(Long labelId, Pageable pageable) {
        Page<Tweet> tweets = tweetRepository.findByLabelId(labelId, pageable);
        return tweets.map(tweetMapper::toResponse);
    }
}