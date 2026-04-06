package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.MarkerMapper;
import com.lizaveta.notebook.model.dto.request.MarkerRequestTo;
import com.lizaveta.notebook.model.dto.response.MarkerResponseTo;
import com.lizaveta.notebook.model.entity.Marker;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.repository.MarkerRepository;
import com.lizaveta.notebook.repository.StoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarkerService {

    private static final String MARKER_NOT_FOUND = "Marker not found with id: ";
    private static final int INVALID_ID_CODE = 40004;

    private final MarkerRepository repository;
    private final StoryRepository storyRepository;
    private final MarkerMapper mapper;

    public MarkerService(
            final MarkerRepository repository,
            final StoryRepository storyRepository,
            final MarkerMapper mapper) {
        this.repository = repository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
    }

    public MarkerResponseTo create(final MarkerRequestTo request) {
        Marker entity = mapper.toEntity(request);
        Marker saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<MarkerResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PageResponseTo<MarkerResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by(Sort.Direction.fromString(sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "desc" : "asc"), sortBy)
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), sort);
        var pageResult = repository.findAll(pageable);
        List<MarkerResponseTo> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponseTo<>(content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getSize(), pageResult.getNumber());
    }

    public MarkerResponseTo findById(final Long id) {
        validateId(id);
        Marker entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MARKER_NOT_FOUND + id));
        return mapper.toResponse(entity);
    }

    public List<MarkerResponseTo> findByStoryId(final Long storyId) {
        validateId(storyId);
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId))
                .getMarkerIds().stream()
                .map(repository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(mapper::toResponse)
                .toList();
    }

    public MarkerResponseTo update(final Long id, final MarkerRequestTo request) {
        validateId(id);
        Marker existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MARKER_NOT_FOUND + id));
        Marker updated = existing.withName(request.name());
        repository.update(updated);
        return mapper.toResponse(updated);
    }

    public void deleteById(final Long id) {
        validateId(id);
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(MARKER_NOT_FOUND + id);
        }
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }
}
