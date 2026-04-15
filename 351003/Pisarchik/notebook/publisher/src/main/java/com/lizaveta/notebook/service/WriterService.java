package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ForbiddenException;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.WriterMapper;
import com.lizaveta.notebook.model.dto.request.WriterRequestTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.repository.WriterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterService {

    private static final String WRITER_NOT_FOUND = "Writer not found with id: ";
    private static final int INVALID_ID_CODE = 40004;

    private final WriterRepository repository;
    private final StoryRepository storyRepository;
    private final WriterMapper mapper;

    public WriterService(
            final WriterRepository repository,
            final StoryRepository storyRepository,
            final WriterMapper mapper) {
        this.repository = repository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
    }

    public WriterResponseTo create(final WriterRequestTo request) {
        if (repository.existsByLogin(request.login())) {
            throw new ForbiddenException("Writer with login '" + request.login() + "' already exists");
        }
        Writer entity = mapper.toEntity(request);
        Writer saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<WriterResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PageResponseTo<WriterResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by(Sort.Direction.fromString(sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "desc" : "asc"), sortBy)
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), sort);
        var pageResult = repository.findAll(pageable);
        List<WriterResponseTo> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponseTo<>(content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getSize(), pageResult.getNumber());
    }

    public WriterResponseTo findById(final Long id) {
        validateId(id);
        Writer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WRITER_NOT_FOUND + id));
        return mapper.toResponse(entity);
    }

    public WriterResponseTo findByStoryId(final Long storyId) {
        validateId(storyId);
        Long writerId = storyRepository.findById(storyId)
                .map(Story::getWriterId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        return findById(writerId);
    }

    public WriterResponseTo update(final Long id, final WriterRequestTo request) {
        validateId(id);
        Writer existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WRITER_NOT_FOUND + id));
        if (!existing.getLogin().equals(request.login()) && repository.existsByLogin(request.login())) {
            throw new ForbiddenException("Writer with login '" + request.login() + "' already exists");
        }
        Writer updated = existing.withLogin(request.login())
                .withPassword(request.password())
                .withFirstname(request.firstname())
                .withLastname(request.lastname());
        repository.update(updated);
        return mapper.toResponse(updated);
    }

    public void deleteById(final Long id) {
        validateId(id);
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(WRITER_NOT_FOUND + id);
        }
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }
}
