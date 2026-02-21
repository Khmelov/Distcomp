package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.NoticeMapper;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.entity.Notice;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.repository.NoticeRepository;
import com.lizaveta.notebook.repository.StoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private static final String NOTICE_NOT_FOUND = "Notice not found with id: ";
    private static final int STORY_NOT_FOUND_CODE = 40002;
    private static final int INVALID_ID_CODE = 40004;

    private final NoticeRepository repository;
    private final StoryRepository storyRepository;
    private final NoticeMapper mapper;

    public NoticeService(
            final NoticeRepository repository,
            final StoryRepository storyRepository,
            final NoticeMapper mapper) {
        this.repository = repository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
    }

    public NoticeResponseTo create(final NoticeRequestTo request) {
        validateStoryExists(request.storyId());
        Notice entity = mapper.toEntity(request);
        Notice saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<NoticeResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PageResponseTo<NoticeResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by(Sort.Direction.fromString(sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "desc" : "asc"), sortBy)
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), sort);
        var pageResult = repository.findAll(pageable);
        List<NoticeResponseTo> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponseTo<>(content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getSize(), pageResult.getNumber());
    }

    public NoticeResponseTo findById(final Long id) {
        validateId(id);
        Notice entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTICE_NOT_FOUND + id));
        return mapper.toResponse(entity);
    }

    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        validateId(storyId);
        validateStoryExists(storyId);
        return repository.findByStoryId(storyId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        validateId(id);
        Notice existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTICE_NOT_FOUND + id));
        validateStoryExists(request.storyId());
        Notice updated = existing.withStoryId(request.storyId()).withContent(request.content());
        repository.update(updated);
        return mapper.toResponse(updated);
    }

    public void deleteById(final Long id) {
        validateId(id);
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(NOTICE_NOT_FOUND + id);
        }
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }

    private void validateStoryExists(final Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new ValidationException("Story not found with id: " + storyId, STORY_NOT_FOUND_CODE);
        }
    }
}
