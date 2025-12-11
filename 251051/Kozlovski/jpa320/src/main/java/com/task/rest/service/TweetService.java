package com.task.rest.service;

import com.task.rest.dto.MarkResponseTo;
import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.exception.ConflictException;
import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import com.task.rest.repository.MarkRepository;
import com.task.rest.repository.TweetRepository;
import com.task.rest.util.TweetMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final MarkRepository markRepository;
    private final TweetMapper tweetMapper;

    public TweetService(TweetRepository tweetRepository, MarkRepository markRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.markRepository = markRepository;
        this.tweetMapper = tweetMapper;
    }

    public TweetResponseTo createTweet(@Valid TweetRequestTo requestTo) {
        List<Mark> markToSave = new ArrayList<>();
        Tweet tweet = tweetMapper.toEntity(requestTo);
        for (String mark : requestTo.getMarks()){
            Mark mark1 = new Mark();
            mark1.setName(mark);
            markToSave.add(markRepository.saveAndFlush(mark1));

        }

        if(tweetRepository.existsByWriterId(requestTo.getWriterId())){
            throw new ConflictException("Tweet with Id '" + requestTo.getWriterId() + "' already exists");
        }
        tweet.setMarks(markToSave);
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

        List<Long> markIds = getTweetById(id).getMarkIds().stream().toList();

        tweetRepository.deleteById(id);
        for (Long markId : markIds){
            if (!tweetRepository.existsByMarkId(markId)){
                markRepository.deleteById(markId);
            }
        }

    }

    public List<TweetResponseTo> getTweetsByMarkId(Long markId) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + markId));

        return tweetRepository.findByMarks(mark).stream()
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
