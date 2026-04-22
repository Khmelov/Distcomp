package com.example.kafkademo.service;

import com.example.kafkademo.entity.Tweet;
import com.example.kafkademo.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    public List<Tweet> findAll() {
        return tweetRepository.findAll();
    }

    public Optional<Tweet> findById(Long id) {
        return tweetRepository.findById(id);
    }

    public Tweet save(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    public void deleteById(Long id) {
        tweetRepository.deleteById(id);
    }

    public List<Tweet> findByCreatorId(Long creatorId) {
        return tweetRepository.findByCreatorId(creatorId);
    }
}