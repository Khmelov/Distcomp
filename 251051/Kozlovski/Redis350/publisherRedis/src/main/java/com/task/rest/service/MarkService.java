package com.task.rest.service;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.model.Mark;
import com.task.rest.repository.MarkRepository;
import com.task.rest.util.MarkMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkService {
    private final MarkRepository markRepository;
    private final MarkMapper markMapper;

    @Autowired
    public MarkService(MarkRepository markRepository, MarkMapper markMapper) {
        this.markRepository = markRepository;
        this.markMapper = markMapper;
    }

    public MarkResponseTo createMark(@Valid MarkRequestTo requestTo) {
        Mark mark = markMapper.toEntity(requestTo);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponse(savedMark);
    }

    public MarkResponseTo getMarkById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + id));
        return markMapper.toResponse(mark);
    }

    public List<MarkResponseTo> getAllMarks() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MarkResponseTo updateMark(Long id, @Valid MarkRequestTo requestTo) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + id));
        markMapper.updateEntityFromDto(requestTo, existingMark);
        Mark updatedMark = markRepository.save(existingMark);
        return markMapper.toResponse(updatedMark);
    }

    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new RuntimeException("Mark not found with id: " + id);
        }
        markRepository.deleteById(id);
    }

    public List<MarkResponseTo> getMarksByTweetId(Long tweetId) {
        return markRepository.findAll().stream()
                .filter(mark -> mark.getTweets().stream()
                        .anyMatch(tweet -> tweet.getId().equals(tweetId)))
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }
}
