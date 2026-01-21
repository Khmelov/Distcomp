package com.example.publisher.service;

import com.example.publisher.config.CacheConfig;
import com.example.publisher.dto.request.MarkRequestTo;
import com.example.publisher.dto.response.MarkResponseTo;
import com.example.publisher.entity.Mark;
import com.example.publisher.exception.NotFoundException;
import com.example.publisher.mapper.MarkMapper;
import com.example.publisher.repository.MarkRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    private static final Logger logger = LoggerFactory.getLogger(MarkService.class);

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private MarkMapper markMapper;

    @Cacheable(value = CacheConfig.CacheNames.MARKS, key = "'all'")
    public List<MarkResponseTo> getAllMarks() {
        logger.info("Fetching all marks from database");
        return markMapper.toResponseList(markRepository.findAll());
    }

    @Cacheable(value = CacheConfig.CacheNames.MARK_BY_ID, key = "#id")
    public MarkResponseTo getMarkById(Long id) {
        logger.info("Fetching mark with id {} from database", id);
        Mark mark = markRepository.findByIdWithStories(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));
        return markMapper.toResponse(mark);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.MARKS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.MARK_BY_ID, key = "#result.id")
    })
    public MarkResponseTo createMark(@Valid MarkRequestTo request) {
        logger.info("Creating new mark with name: {}", request.getName());

        Mark mark = markMapper.toEntity(request);
        mark.setCreatedAt(LocalDateTime.now());
        mark.setModifiedAt(LocalDateTime.now());

        Mark savedMark = markRepository.save(mark);
        logger.info("Mark created with id: {}", savedMark.getId());

        return markMapper.toResponse(savedMark);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.MARKS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.MARK_BY_ID, key = "#id")
    })
    public MarkResponseTo updateMark(Long id, @Valid MarkRequestTo request) {
        logger.info("Updating mark with id: {}", id);

        Mark existingMark = markRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mark not found with id: " + id, 40403));

        markMapper.updateEntity(request, existingMark);
        existingMark.setModifiedAt(LocalDateTime.now());

        Mark updatedMark = markRepository.save(existingMark);
        logger.info("Mark with id {} updated", id);

        return markMapper.toResponse(updatedMark);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CacheNames.MARKS, allEntries = true),
            @CacheEvict(value = CacheConfig.CacheNames.MARK_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConfig.CacheNames.MARKS_BY_STORY, allEntries = true)
    })
    public void deleteMark(Long id) {
        logger.info("Deleting mark with id: {}", id);

        if (!markRepository.existsById(id)) {
            throw new NotFoundException("Mark not found with id: " + id, 40403);
        }
        markRepository.deleteById(id);
        logger.info("Mark with id {} deleted", id);
    }

    @Cacheable(value = CacheConfig.CacheNames.MARKS_BY_STORY, key = "#storyId")
    public List<MarkResponseTo> getMarksByStoryId(Long storyId) {
        logger.info("Fetching marks for story id {} from database", storyId);

        List<Mark> marks = markRepository.findByStoryId(storyId);
        return markMapper.toResponseList(marks);
    }
}