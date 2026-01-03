package com.task.rest.service;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.mapper.MarkMapper;
import com.task.rest.model.Mark;
import com.task.rest.repository.MarkRepository;
import com.task.rest.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final MarkRepository repository;
    private final TweetRepository tweetRepository;
    private final MarkMapper mapper;

    public MarkResponseTo create(MarkRequestTo request) {
        Mark mark = mapper.toEntity(request);
        mark = repository.save(mark);
        return mapper.toDto(mark);
    }

    public MarkResponseTo getById(Long id) {
        Mark mark = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mark not found with id: " + id, "40421"));
        return mapper.toDto(mark);
    }

    public List<MarkResponseTo> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public MarkResponseTo update(MarkRequestTo request) {
        if (request.getId() == null || !repository.existsById(request.getId())) {
            throw new ResourceNotFoundException(
                    "Mark not found with id: " + request.getId(), "40422");
        }
        Mark mark = mapper.toEntity(request);
        mark = repository.update(mark);
        return mapper.toDto(mark);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Mark not found with id: " + id, "40423");
        }
        repository.deleteById(id);
    }

    public List<MarkResponseTo> getByTweetId(Long tweetId) {
        return tweetRepository.findById(tweetId)
                .map(tweet -> tweet.getMarkIds().stream()
                        .map(markId -> repository.findById(markId))
                        .filter(opt -> opt.isPresent())
                        .map(opt -> opt.get())
                        .map(mapper::toDto)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tweet not found with id: " + tweetId, "40411"));
    }
}