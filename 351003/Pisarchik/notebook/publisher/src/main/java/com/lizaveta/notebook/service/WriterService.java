package com.lizaveta.notebook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lizaveta.notebook.cache.NotebookCacheKeys;
import com.lizaveta.notebook.cache.PublisherRedisCache;
import com.lizaveta.notebook.exception.ForbiddenException;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.WriterMapper;
import com.lizaveta.notebook.model.UserRole;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WriterService {

    private static final String WRITER_NOT_FOUND = "Writer not found with id: ";
    private static final int INVALID_ID_CODE = 40004;
    private static final int INVALID_ROLE_CODE = 40005;

    private final WriterRepository repository;
    private final StoryRepository storyRepository;
    private final WriterMapper mapper;
    private final PublisherRedisCache redisCache;
    private final PasswordEncoder passwordEncoder;

    public WriterService(
            final WriterRepository repository,
            final StoryRepository storyRepository,
            final WriterMapper mapper,
            final PublisherRedisCache redisCache,
            final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.storyRepository = storyRepository;
        this.mapper = mapper;
        this.redisCache = redisCache;
        this.passwordEncoder = passwordEncoder;
    }

    public WriterResponseTo create(final WriterRequestTo request) {
        if (repository.existsByLogin(request.login())) {
            throw new ForbiddenException("Writer with login '" + request.login() + "' already exists");
        }
        UserRole role = parseRoleForCreate(request.role());
        Writer entity = mapper.toEntity(request)
                .withPassword(passwordEncoder.encode(request.password()))
                .withRole(role);
        Writer saved = repository.save(entity);
        WriterResponseTo response = mapper.toResponse(saved);
        redisCache.evictKeyPattern(NotebookCacheKeys.WRITER_PREFIX + "*");
        return response;
    }

    public List<WriterResponseTo> findAll() {
        String key = NotebookCacheKeys.writerAll();
        return redisCache.get(key, new TypeReference<List<WriterResponseTo>>() {
        }).orElseGet(() -> {
            List<WriterResponseTo> loaded = repository.findAll().stream()
                    .map(mapper::toResponse)
                    .toList();
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public PageResponseTo<WriterResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by(Sort.Direction.fromString(sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "desc" : "asc"), sortBy)
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), sort);
        String key = NotebookCacheKeys.writerPage(pageable.getPageNumber(), pageable.getPageSize(), sortBy, sortOrder);
        return redisCache.get(key, new TypeReference<PageResponseTo<WriterResponseTo>>() {
        }).orElseGet(() -> {
            var pageResult = repository.findAll(pageable);
            List<WriterResponseTo> content = pageResult.getContent().stream()
                    .map(mapper::toResponse)
                    .toList();
            PageResponseTo<WriterResponseTo> loaded = new PageResponseTo<>(
                    content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getSize(), pageResult.getNumber());
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public WriterResponseTo findById(final Long id) {
        validateId(id);
        String key = NotebookCacheKeys.writerById(id);
        return redisCache.get(key, WriterResponseTo.class).orElseGet(() -> {
            Writer entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(WRITER_NOT_FOUND + id));
            WriterResponseTo loaded = mapper.toResponse(entity);
            redisCache.put(key, loaded);
            return loaded;
        });
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
        UserRole newRole = parseRoleForUpdate(request.role(), existing.getRole());
        Writer updated = existing.withLogin(request.login())
                .withPassword(passwordEncoder.encode(request.password()))
                .withFirstname(request.firstname())
                .withLastname(request.lastname())
                .withRole(newRole);
        repository.update(updated);
        WriterResponseTo response = mapper.toResponse(updated);
        redisCache.evictKeyPattern(NotebookCacheKeys.WRITER_PREFIX + "*");
        return response;
    }

    public void deleteById(final Long id) {
        validateId(id);
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(WRITER_NOT_FOUND + id);
        }
        redisCache.evictKeyPattern(NotebookCacheKeys.WRITER_PREFIX + "*");
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }

    private UserRole parseRoleForCreate(final String role) {
        if (role == null || role.isBlank()) {
            return UserRole.CUSTOMER;
        }
        return parseRoleOrThrow(role);
    }

    private UserRole parseRoleForUpdate(final String role, final UserRole previous) {
        if (role == null || role.isBlank()) {
            return previous;
        }
        return parseRoleOrThrow(role);
    }

    private UserRole parseRoleOrThrow(final String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid role value: " + role, INVALID_ROLE_CODE);
        }
    }
}
