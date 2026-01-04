package com.task.rest.service;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.model.Notice;
import com.task.rest.model.Tweet;
import com.task.rest.repository.NoticeRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final TweetRepository tweetRepository;

    @Transactional(readOnly = true)
    public NoticeResponseTo getById(Long id) {
        log.info("Getting notice by id: {}", id);
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notice not found with id: {}", id);
                    return new ResourceNotFoundException("Notice not found with id: " + id);
                });
        return mapToResponse(notice);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseTo> getAll(Pageable pageable) {
        log.info("Getting all notices with pageable: {}", pageable);
        return noticeRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getByTweetId(Long tweetId) {
        log.info("Getting notices by tweet id: {}", tweetId);

        if (!tweetRepository.existsById(tweetId)) {
            log.error("Tweet not found with id: {}", tweetId);
            throw new ResourceNotFoundException("Tweet not found with id: " + tweetId);
        }

        return noticeRepository.findByTweetId(tweetId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NoticeResponseTo create(NoticeRequestTo requestTo) {
        log.info("Creating new notice for tweet id: {}", requestTo.getTweetId());

        Tweet tweet = tweetRepository.findById(requestTo.getTweetId())
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", requestTo.getTweetId());
                    return new ResourceNotFoundException("Tweet not found with id: " + requestTo.getTweetId());
                });

        Notice notice = new Notice();
        notice.setTweet(tweet);
        notice.setContent(requestTo.getContent());

        notice = noticeRepository.save(notice);
        log.info("Notice created with id: {}", notice.getId());
        return mapToResponse(notice);
    }

    public NoticeResponseTo update(Long id, NoticeRequestTo requestTo) {
        log.info("Updating notice with id: {}", id);

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notice not found with id: {}", id);
                    return new ResourceNotFoundException("Notice not found with id: " + id);
                });

        Tweet tweet = tweetRepository.findById(requestTo.getTweetId())
                .orElseThrow(() -> {
                    log.error("Tweet not found with id: {}", requestTo.getTweetId());
                    return new ResourceNotFoundException("Tweet not found with id: " + requestTo.getTweetId());
                });

        notice.setTweet(tweet);
        notice.setContent(requestTo.getContent());
        notice = noticeRepository.save(notice);
        log.info("Notice updated with id: {}", id);
        return mapToResponse(notice);
    }

    public void delete(Long id) {
        log.info("Deleting notice with id: {}", id);

        if (!noticeRepository.existsById(id)) {
            log.error("Notice not found with id: {}", id);
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }

        noticeRepository.deleteById(id);
        log.info("Notice deleted with id: {}", id);
    }

    private NoticeResponseTo mapToResponse(Notice notice) {
        return new NoticeResponseTo(
                notice.getId(),
                notice.getTweet().getId(),
                notice.getContent(),
                notice.getCreated(),
                notice.getModified()
        );
    }
}