// MarkService.java
package com.example.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.dto.request.MarkRequestTo;
import com.example.dto.response.MarkResponseTo;
import com.example.exception.DuplicateException;
import com.example.exception.NotFoundException;
import com.example.mapper.MarkMapper;
import com.example.model.Mark;
import com.example.model.Story;
import com.example.repository.InMemoryMarkRepository;
import com.example.repository.InMemoryStoryRepository;

import java.util.List;

@Service
@Validated
public class MarkService {

    @Autowired
    private InMemoryMarkRepository markRepository;

    @Autowired
    private InMemoryStoryRepository storyRepository;

    @Autowired
    private MarkMapper markMapper;

    public List<MarkResponseTo> getAllMarks() {
        return markMapper.toResponseList(markRepository.findAll());
    }

    public MarkResponseTo getMarkById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));
        return markMapper.toResponse(mark);
    }

    public MarkResponseTo createMark(@Valid MarkRequestTo request) {
        // Check if mark name already exists
        markRepository.findByName(request.getName())
                .ifPresent(mark -> {
                    throw new DuplicateException("Mark with name '" + request.getName() + "' already exists", 40902);
                });

        Mark mark = markMapper.toEntity(request);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponse(savedMark);
    }

    public MarkResponseTo updateMark(Long id, @Valid MarkRequestTo request) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));

        // Check if mark name already exists for another mark
        markRepository.findByName(request.getName())
                .ifPresent(mark -> {
                    if (!mark.getId().equals(id)) {
                        throw new DuplicateException("Mark with name '" + request.getName() + "' already exists", 40902);
                    }
                });

        existingMark.setName(request.getName());

        Mark updatedMark = markRepository.update(existingMark);
        return markMapper.toResponse(updatedMark);
    }

    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new NotFoundException("Mark not found with id: " + id, 40403);
        }

        // Remove this mark from all stories
        List<Story> storiesWithMark = storyRepository.findByMarkIdsContaining(id);
        for (Story story : storiesWithMark) {
            story.removeMark(id);
            storyRepository.update(story);
        }

        markRepository.deleteById(id);
    }

    public List<MarkResponseTo> getMarksByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId, 40402));

        List<Mark> marks = markRepository.findAll().stream()
                .filter(mark -> story.getMarkIds().contains(mark.getId()))
                .toList();

        return markMapper.toResponseList(marks);
    }
}