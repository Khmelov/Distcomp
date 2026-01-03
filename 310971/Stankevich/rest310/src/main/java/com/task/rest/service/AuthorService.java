package com.task.rest.service;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.AuthorMapper;
import com.task.rest.model.Author;
import com.task.rest.repository.AuthorRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository repository;
    private final TweetRepository tweetRepository;
    private final AuthorMapper mapper;

    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = mapper.toEntity(request);
        author = repository.save(author);
        return mapper.toDto(author);
    }

    public AuthorResponseTo getById(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Author not found with id: " + id, "40401"));
        return mapper.toDto(author);
    }

    public List<AuthorResponseTo> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public AuthorResponseTo update(AuthorRequestTo request) {
        if (request.getId() == null || !repository.existsById(request.getId())) {
            throw new ResourceNotFoundException(
                    "Author not found with id: " + request.getId(), "40402");
        }
        Author author = mapper.toEntity(request);
        author = repository.update(author);
        return mapper.toDto(author);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Author not found with id: " + id, "40403");
        }
        repository.deleteById(id);
    }

    public AuthorResponseTo getByTweetId(Long tweetId) {
        return tweetRepository.findById(tweetId)
                .map(tweet -> repository.findById(tweet.getAuthorId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Author not found for tweet id: " + tweetId, "40404")))
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + tweetId, "40411"));
    }
}