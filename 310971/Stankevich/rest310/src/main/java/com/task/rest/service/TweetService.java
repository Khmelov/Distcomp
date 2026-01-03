package com.task.rest.service;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.TweetMapper;
import com.task.rest.model.Tweet;
import com.task.rest.repository.AuthorRepository;
import com.task.rest.repository.MarkRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository repository;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;
    private final TweetMapper mapper;

    public TweetResponseTo create(TweetRequestTo request) {
        if (!authorRepository.existsById(request.getAuthorId())) {
            throw new ResourceNotFoundException(
                    "Author not found with id: " + request.getAuthorId(), "40404");
        }

        Tweet tweet = mapper.toEntity(request);
        tweet.setCreated(LocalDateTime.now());
        tweet.setModified(LocalDateTime.now());
        tweet = repository.save(tweet);
        return mapper.toDto(tweet);
    }

    public TweetResponseTo getById(Long id) {
        Tweet tweet = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + id, "40411"));
        return mapper.toDto(tweet);
    }

    public List<TweetResponseTo> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public TweetResponseTo update(TweetRequestTo request) {
        if (request.getId() == null || !repository.existsById(request.getId())) {
            throw new ResourceNotFoundException(
                    "Tweet not found with id: " + request.getId(), "40412");
        }

        if (!authorRepository.existsById(request.getAuthorId())) {
            throw new ResourceNotFoundException(
                    "Author not found with id: " + request.getAuthorId(), "40404");
        }

        Tweet existingTweet = repository.findById(request.getId()).get();
        Tweet tweet = mapper.toEntity(request);
        tweet.setCreated(existingTweet.getCreated());
        tweet.setModified(LocalDateTime.now());
        tweet.setMarkIds(existingTweet.getMarkIds());
        tweet = repository.update(tweet);
        return mapper.toDto(tweet);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Tweet not found with id: " + id, "40413");
        }
        repository.deleteById(id);
    }

    public List<TweetResponseTo> search(List<String> markNames, List<Long> markIds,
                                        String authorLogin, String title, String content) {
        List<Tweet> tweets = repository.findAll();

        if (authorLogin != null && !authorLogin.isEmpty()) {
            Long authorId = authorRepository.findByLogin(authorLogin)
                    .map(author -> author.getId())
                    .orElse(null);
            if (authorId != null) {
                tweets = tweets.stream()
                        .filter(tweet -> tweet.getAuthorId().equals(authorId))
                        .collect(Collectors.toList());
            } else {
                return List.of();
            }
        }

        if (markNames != null && !markNames.isEmpty()) {
            List<Long> matchingMarkIds = markNames.stream()
                    .map(name -> markRepository.findByName(name))
                    .filter(opt -> opt.isPresent())
                    .map(opt -> opt.get().getId())
                    .collect(Collectors.toList());

            if (!matchingMarkIds.isEmpty()) {
                tweets = tweets.stream()
                        .filter(tweet -> tweet.getMarkIds().stream()
                                .anyMatch(matchingMarkIds::contains))
                        .collect(Collectors.toList());
            }
        }

        if (markIds != null && !markIds.isEmpty()) {
            tweets = tweets.stream()
                    .filter(tweet -> tweet.getMarkIds().stream()
                            .anyMatch(markIds::contains))
                    .collect(Collectors.toList());
        }

        if (title != null && !title.isEmpty()) {
            tweets = tweets.stream()
                    .filter(tweet -> tweet.getTitle().toLowerCase()
                            .contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (content != null && !content.isEmpty()) {
            tweets = tweets.stream()
                    .filter(tweet -> tweet.getContent().toLowerCase()
                            .contains(content.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return mapper.toDtoList(tweets);
    }

    public void addMarkToTweet(Long tweetId, Long markId) {
        Tweet tweet = repository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + tweetId, "40411"));

        if (!markRepository.existsById(markId)) {
            throw new ResourceNotFoundException(
                    "Mark not found with id: " + markId, "40421");
        }

        tweet.getMarkIds().add(markId);
        repository.update(tweet);
    }

    public void removeMarkFromTweet(Long tweetId, Long markId) {
        Tweet tweet = repository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + tweetId, "40411"));

        tweet.getMarkIds().remove(markId);
        repository.update(tweet);
    }
}