package com.example.service;

import com.example.dto.request.MarkRequestTo;
import com.example.dto.response.MarkResponseTo;
import com.example.entity.Mark;
import com.example.exception.DuplicateException;
import com.example.exception.NotFoundException;
import com.example.mapper.MarkMapper;
import com.example.repository.MarkRepository;
import com.example.repository.StoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class MarkService {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private MarkMapper markMapper;

    public List<MarkResponseTo> getAllMarks() {
        return markMapper.toResponseList(markRepository.findAll());
    }

    public Page<MarkResponseTo> getAllMarks(Pageable pageable) {
        return markRepository.findAll(pageable)
                .map(markMapper::toResponse);
    }

    public MarkResponseTo getMarkById(Long id) {
        Mark mark = markRepository.findByIdWithStories(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));
        return markMapper.toResponse(mark);
    }

    @Transactional
    public MarkResponseTo createMark(@Valid MarkRequestTo request) {
        // Проверка уникальности имени метки
        if (markRepository.existsByName(request.getName())) {
            throw new DuplicateException("Mark with name '" + request.getName() + "' already exists", 40902);
        }

        Mark mark = markMapper.toEntity(request);
        mark.setCreatedAt(LocalDateTime.now());
        mark.setModifiedAt(LocalDateTime.now());

        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponse(savedMark);
    }

    @Transactional
    public MarkResponseTo updateMark(Long id, @Valid MarkRequestTo request) {
        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));

        // Проверка уникальности имени метки (исключая текущую метку)
        if (markRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateException("Mark with name '" + request.getName() + "' already exists", 40902);
        }

        markMapper.updateEntity(request, existingMark);
        existingMark.setModifiedAt(LocalDateTime.now());

        Mark updatedMark = markRepository.save(existingMark);
        return markMapper.toResponse(updatedMark);
    }

    @Transactional
    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new NotFoundException("Mark not found with id: " + id, 40403);
        }
        markRepository.deleteById(id);
    }

    public List<MarkResponseTo> getMarksByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }

        List<Mark> marks = markRepository.findByStoryId(storyId);
        return markMapper.toResponseList(marks);
    }

    public Page<MarkResponseTo> getMarksByStoryId(Long storyId, Pageable pageable) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }

        return markRepository.findByStoryId(storyId, pageable)
                .map(markMapper::toResponse);
    }

    public Page<MarkResponseTo> searchMarks(String name, Pageable pageable) {
        return markRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(markMapper::toResponse);
    }
}