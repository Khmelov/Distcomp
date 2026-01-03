package com.task.rest.service;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.NoticeMapper;
import com.task.rest.model.Notice;
import com.task.rest.repository.NoticeRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;
    private final TweetRepository tweetRepository;
    private final NoticeMapper mapper;

    public NoticeResponseTo create(NoticeRequestTo request) {
        if (!tweetRepository.existsById(request.getTweetId())) {
            throw new ResourceNotFoundException(
                    "Tweet not found with id: " + request.getTweetId(), "40411");
        }

        Notice notice = mapper.toEntity(request);
        notice = repository.save(notice);
        return mapper.toDto(notice);
    }

    public NoticeResponseTo getById(Long id) {
        Notice notice = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notice not found with id: " + id, "40431"));
        return mapper.toDto(notice);
    }

    public List<NoticeResponseTo> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public NoticeResponseTo update(NoticeRequestTo request) {
        if (request.getId() == null || !repository.existsById(request.getId())) {
            throw new ResourceNotFoundException(
                    "Notice not found with id: " + request.getId(), "40432");
        }

        if (!tweetRepository.existsById(request.getTweetId())) {
            throw new ResourceNotFoundException(
                    "Tweet not found with id: " + request.getTweetId(), "40411");
        }

        Notice notice = mapper.toEntity(request);
        notice = repository.update(notice);
        return mapper.toDto(notice);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Notice not found with id: " + id, "40433");
        }
        repository.deleteById(id);
    }

    public List<NoticeResponseTo> getByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException(
                    "Tweet not found with id: " + tweetId, "40411");
        }
        return mapper.toDtoList(repository.findByTweetId(tweetId));
    }
}