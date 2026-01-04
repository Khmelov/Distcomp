package com.task.rest.service;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;

    @Transactional(readOnly = true)
    public TweetResponseTo getById(Long id) {
        log.info("Getting tweet by id: {}", id);
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", id);
                    return new ResourceNotFoundException("Tweet not found with id: " + id);
                });
        return mapToResponse(tweet);
    }

    @Transactional(readOnly = true)
    public Page<TweetResponseTo> getAll(Pageable pageable) {
        log.info("Getting all tweets with pageable: {}", pageable);
        return tweetRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TweetResponseTo> getByAuthorId(Long authorId) {
        log.info("Getting tweets by author id: {}", authorId);
        if (!authorRepository.existsById(authorId)) {
            log.error("Author not found with id: {}", authorId);
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }
        return tweetRepository.findByAuthorId(authorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TweetResponseTo create(TweetRequestTo requestTo) {
        log.info("Creating new tweet for author id: {}", requestTo.getAuthorId());

        if (tweetRepository.existsByTitle(requestTo.getTitle())) {
            log.error("Tweet with title already exists: {}", requestTo.getTitle());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Tweet with title already exists: " + requestTo.getTitle());
        }

        Author author = authorRepository.findById(requestTo.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", requestTo.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + requestTo.getAuthorId());
                });

        List<Mark> marks = processMarks(requestTo);

        Tweet tweet = new Tweet();
        tweet.setAuthor(author);
        tweet.setTitle(requestTo.getTitle());
        tweet.setContent(requestTo.getContent());
        tweet.setMarks(marks);

        tweet = tweetRepository.save(tweet);
        log.info("Tweet created with id: {}", tweet.getId());

        return mapToResponse(tweet);
    }

    public TweetResponseTo update(Long id, TweetRequestTo requestTo) {
        log.info("Updating tweet with id: {}", id);

        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", id);
                    return new ResourceNotFoundException("Tweet not found with id: " + id);
                });

        tweetRepository.findByTitle(requestTo.getTitle()).ifPresent(existingTweet -> {
            if (!existingTweet.getId().equals(id)) {
                log.error("Tweet with title already exists: {}", requestTo.getTitle());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Tweet with title already exists: " + requestTo.getTitle());
            }
        });

        Author author = authorRepository.findById(requestTo.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", requestTo.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + requestTo.getAuthorId());
                });

        List<Mark> oldMarks = new ArrayList<>(tweet.getMarks());

        tweet.getMarks().clear();

        List<Mark> newMarks = processMarks(requestTo);

        tweet.setAuthor(author);
        tweet.setTitle(requestTo.getTitle());
        tweet.setContent(requestTo.getContent());
        tweet.setMarks(newMarks);

        tweet = tweetRepository.save(tweet);
        log.info("Tweet updated with id: {}", id);

        deleteOrphanMarks(oldMarks, newMarks);

        return mapToResponse(tweet);
    }

    public void delete(Long id) {
        log.info("Deleting tweet with id: {}", id);

        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", id);
                    return new ResourceNotFoundException("Tweet not found with id: " + id);
                });

        List<Mark> marksToCheck = new ArrayList<>(tweet.getMarks());

        tweetRepository.deleteById(id);
        log.info("Tweet deleted with id: {}", id);

        for (Mark mark : marksToCheck) {
            markRepository.findById(mark.getId()).ifPresent(freshMark -> {
                long count = tweetRepository.countByMarksId(freshMark.getId());
                if (count == 0) {
                    log.info("Deleting orphan mark with id: {} and name: {}", freshMark.getId(), freshMark.getName());
                    markRepository.delete(freshMark);
                }
            });
        }
    }

    private void deleteOrphanMarks(List<Mark> oldMarks, List<Mark> newMarks) {
        List<Long> newMarkIds = newMarks.stream().map(Mark::getId).collect(Collectors.toList());

        for (Mark oldMark : oldMarks) {
            if (!newMarkIds.contains(oldMark.getId())) {
                markRepository.findById(oldMark.getId()).ifPresent(freshMark -> {
                    long count = tweetRepository.countByMarksId(freshMark.getId());
                    if (count == 0) {
                        log.info("Deleting orphan mark with id: {} and name: {}", freshMark.getId(), freshMark.getName());
                        markRepository.delete(freshMark);
                    }
                });
            }
        }
    }

    private List<Mark> processMarks(TweetRequestTo requestTo) {
        List<Mark> marks = new ArrayList<>();

        if (requestTo.getMarks() != null && !requestTo.getMarks().isEmpty()) {
            for (String markName : requestTo.getMarks()) {
                Mark mark = markRepository.findByName(markName)
                        .orElseGet(() -> {
                            log.info("Creating new mark with name: {}", markName);
                            Mark newMark = new Mark();
                            newMark.setName(markName);
                            return markRepository.save(newMark);
                        });
                marks.add(mark);
            }
        }

        return marks;
    }


    private TweetResponseTo mapToResponse(Tweet tweet) {
        List<String> marks = tweet.getMarks() != null
                ? tweet.getMarks().stream()
                .map(Mark::getName)  // ← Изменено с getId() на getName()
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new TweetResponseTo(
                tweet.getId(),
                tweet.getAuthor().getId(),
                tweet.getTitle(),
                tweet.getContent(),
                tweet.getCreated(),
                tweet.getModified(),
                marks  // ← Теперь List<String>, не List<Long>
        );
    }

}
