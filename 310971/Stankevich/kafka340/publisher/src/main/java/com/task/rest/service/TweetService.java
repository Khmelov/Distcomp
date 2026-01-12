package com.task.rest.service;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.TweetMapper;
import com.task.rest.model.Author;
import com.task.rest.model.Mark;
import com.task.rest.model.Tweet;
import com.task.rest.repository.AuthorRepository;
import com.task.rest.repository.MarkRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TweetService {

    private final TweetRepository tweetRepository;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;
    private final TweetMapper tweetMapper;

    public TweetResponseTo getById(Long id) {
        log.info("Getting tweet by id: {}", id);
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", id);
                    return new ResourceNotFoundException("Tweet not found with id: " + id);
                });
        return tweetMapper.toResponseTo(tweet);
    }

    public Page<TweetResponseTo> getAll(Pageable pageable) {
        log.info("Getting all tweets with pagination");
        return tweetRepository.findAll(pageable)
                .map(tweetMapper::toResponseTo);
    }

    public List<TweetResponseTo> getAllList() {
        log.info("Getting all tweets as list");
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponseTo)
                .collect(Collectors.toList());
    }


    public List<TweetResponseTo> getByAuthorId(Long authorId) {
        log.info("Getting tweets by author id: {}", authorId);

        if (!authorRepository.existsById(authorId)) {
            log.error("Author not found with id: {}", authorId);
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }

        return tweetRepository.findByAuthorId(authorId).stream()
                .map(tweetMapper::toResponseTo)
                .toList();
    }

    public TweetResponseTo create(TweetRequestTo requestTo) {
        log.info("Creating tweet with title: {}", requestTo.getTitle());

        Author author = authorRepository.findById(requestTo.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", requestTo.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + requestTo.getAuthorId());
                });

        Tweet tweet = new Tweet();
        tweet.setAuthor(author);
        tweet.setTitle(requestTo.getTitle());
        tweet.setContent(requestTo.getContent());

        if (requestTo.getMarks() != null && !requestTo.getMarks().isEmpty()) {
            Set<Mark> marks = new HashSet<>();
            for (String markName : requestTo.getMarks()) {
                Mark mark = markRepository.findByName(markName)
                        .orElseGet(() -> {
                            Mark newMark = new Mark();
                            newMark.setName(markName);
                            return markRepository.save(newMark);
                        });
                marks.add(mark);
            }
            tweet.setMarks(marks);
        }

        Tweet saved = tweetRepository.save(tweet);
        log.info("Tweet created successfully with id: {}", saved.getId());

        return tweetMapper.toResponseTo(saved);
    }

    public TweetResponseTo update(Long id, TweetRequestTo requestTo) {
        log.info("Updating tweet with id: {}", id);

        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", id);
                    return new ResourceNotFoundException("Tweet not found with id: " + id);
                });

        Author author = authorRepository.findById(requestTo.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", requestTo.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + requestTo.getAuthorId());
                });

        tweet.setTitle(requestTo.getTitle());
        tweet.setContent(requestTo.getContent());
        tweet.setAuthor(author);

        if (requestTo.getMarks() != null && !requestTo.getMarks().isEmpty()) {
            Set<Mark> marks = new HashSet<>();
            for (String markName : requestTo.getMarks()) {
                Mark mark = markRepository.findByName(markName)
                        .orElseGet(() -> {
                            Mark newMark = new Mark();
                            newMark.setName(markName);
                            return markRepository.save(newMark);
                        });
                marks.add(mark);
            }
            tweet.setMarks(marks);
        } else {
            tweet.setMarks(new HashSet<>());
        }

        Tweet updated = tweetRepository.save(tweet);
        log.info("Tweet updated successfully with id: {}", id);

        return tweetMapper.toResponseTo(updated);
    }

    public void delete(Long id) {
        log.info("Deleting tweet with id: {}", id);

        if (!tweetRepository.existsById(id)) {
            log.error("Tweet not found with id: {}", id);
            throw new ResourceNotFoundException("Tweet not found with id: " + id);
        }

        tweetRepository.deleteById(id);
        log.info("Tweet deleted successfully with id: {}", id);
    }
}
