package com.lizaveta.notebook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lizaveta.notebook.cache.NotebookCacheKeys;
import com.lizaveta.notebook.cache.PublisherRedisCache;
import com.lizaveta.notebook.exception.ForbiddenException;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.StoryMapper;
import com.lizaveta.notebook.model.dto.request.StoryRequestTo;
import com.lizaveta.notebook.model.dto.response.StoryResponseTo;
import com.lizaveta.notebook.model.entity.Marker;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.repository.MarkerRepository;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.repository.WriterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StoryService {

    private static final String STORY_NOT_FOUND = "Story not found with id: ";
    private static final int WRITER_NOT_FOUND_CODE = 40001;
    private static final int MARKER_NOT_FOUND_CODE = 40003;
    private static final int INVALID_ID_CODE = 40004;

    private final StoryRepository repository;
    private final WriterRepository writerRepository;
    private final MarkerRepository markerRepository;
    private final StoryMapper mapper;
    private final PublisherRedisCache redisCache;

    public StoryService(
            final StoryRepository repository,
            final WriterRepository writerRepository,
            final MarkerRepository markerRepository,
            final StoryMapper mapper,
            final PublisherRedisCache redisCache) {
        this.repository = repository;
        this.writerRepository = writerRepository;
        this.markerRepository = markerRepository;
        this.mapper = mapper;
        this.redisCache = redisCache;
    }

    public StoryResponseTo create(final StoryRequestTo request) {
        validateWriterExists(request.writerId());
        Set<Long> resolvedMarkerIds = resolveMarkerIds(request);
        validateMarkerIdsExist(resolvedMarkerIds);
        if (repository.existsByWriterIdAndTitle(request.writerId(), request.title())) {
            throw new ForbiddenException("Story with title '" + request.title() + "' already exists for this writer");
        }
        LocalDateTime now = LocalDateTime.now();
        Story toSave = new Story(
                null, request.writerId(), request.title(), request.content(),
                now, now, resolvedMarkerIds);
        Story saved = repository.save(toSave);
        StoryResponseTo response = mapper.toResponse(saved);
        evictAfterStorySave(saved.getId());
        return response;
    }

    public List<StoryResponseTo> findAll() {
        String key = NotebookCacheKeys.storyAll();
        return redisCache.get(key, new TypeReference<List<StoryResponseTo>>() {
        }).orElseGet(() -> {
            List<StoryResponseTo> loaded = repository.findAll().stream()
                    .map(mapper::toResponse)
                    .toList();
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public PageResponseTo<StoryResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = sortBy != null && !sortBy.isBlank()
                ? Sort.by(Sort.Direction.fromString(sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "desc" : "asc"), sortBy)
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), sort);
        String key = NotebookCacheKeys.storyPage(pageable.getPageNumber(), pageable.getPageSize(), sortBy, sortOrder);
        return redisCache.get(key, new TypeReference<PageResponseTo<StoryResponseTo>>() {
        }).orElseGet(() -> {
            var pageResult = repository.findAll(pageable);
            List<StoryResponseTo> content = pageResult.getContent().stream()
                    .map(mapper::toResponse)
                    .toList();
            PageResponseTo<StoryResponseTo> loaded = new PageResponseTo<>(
                    content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getSize(), pageResult.getNumber());
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public StoryResponseTo findById(final Long id) {
        validateId(id);
        String key = NotebookCacheKeys.storyById(id);
        return redisCache.get(key, StoryResponseTo.class).orElseGet(() -> {
            Story entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(STORY_NOT_FOUND + id));
            StoryResponseTo loaded = mapper.toResponse(entity);
            redisCache.put(key, loaded);
            return loaded;
        });
    }

    public StoryResponseTo update(final Long id, final StoryRequestTo request) {
        validateId(id);
        Story existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STORY_NOT_FOUND + id));
        validateWriterExists(request.writerId());
        Set<Long> resolvedMarkerIds = resolveMarkerIds(request);
        validateMarkerIdsExist(resolvedMarkerIds);
        if (repository.existsByWriterIdAndTitleAndIdNot(request.writerId(), request.title(), id)) {
            throw new ForbiddenException("Story with title '" + request.title() + "' already exists for this writer");
        }
        Story updated = existing.withWriterId(request.writerId())
                .withTitle(request.title())
                .withContent(request.content())
                .withMarkerIds(resolvedMarkerIds)
                .withModified(LocalDateTime.now());
        repository.update(updated);
        StoryResponseTo response = mapper.toResponse(updated);
        evictAfterStorySave(id);
        return response;
    }

    public void deleteById(final Long id) {
        validateId(id);
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(STORY_NOT_FOUND + id);
        }
        evictAfterStoryDelete(id);
    }

    private void evictAfterStorySave(final long storyId) {
        redisCache.evictKeyPattern(NotebookCacheKeys.STORY_PREFIX + "*");
        redisCache.evictKey(NotebookCacheKeys.markerByStory(storyId));
        redisCache.evictKey(NotebookCacheKeys.noticeByStory(storyId));
    }

    private void evictAfterStoryDelete(final long storyId) {
        evictAfterStorySave(storyId);
        redisCache.evictKeyPattern(NotebookCacheKeys.NOTICE_PREFIX + "*");
    }

    public List<StoryResponseTo> findByMarkerIdsAndWriterLoginAndTitleAndContent(
            final Set<Long> markerIds,
            final String writerLogin,
            final String title,
            final String content) {
        return repository.findAll().stream()
                .filter(s -> matchesFilter(s, markerIds, writerLogin, title, content))
                .map(mapper::toResponse)
                .toList();
    }

    private boolean matchesFilter(
            final Story story,
            final Set<Long> markerIds,
            final String writerLogin,
            final String title,
            final String content) {
        if (markerIds != null && !markerIds.isEmpty() && !story.getMarkerIds().containsAll(markerIds)) {
            return false;
        }
        if (writerLogin != null && !writerLogin.isBlank()) {
            Writer writer = writerRepository.findById(story.getWriterId()).orElse(null);
            if (writer == null || !writerLogin.equals(writer.getLogin())) {
                return false;
            }
        }
        if (title != null && !title.isBlank() && !title.equals(story.getTitle())) {
            return false;
        }
        if (content != null && !content.isBlank() && !content.equals(story.getContent())) {
            return false;
        }
        return true;
    }

    private void validateWriterExists(final Long writerId) {
        if (!writerRepository.existsById(writerId)) {
            throw new ValidationException("Writer not found with id: " + writerId, WRITER_NOT_FOUND_CODE);
        }
    }

    private Set<Long> resolveMarkerIds(final StoryRequestTo request) {
        Set<Long> ids = new HashSet<>();
        if (request.markerIds() != null) {
            ids.addAll(request.markerIds());
        }
        if (request.markerNames() != null) {
            for (String name : request.markerNames()) {
                if (name == null || name.isBlank()) {
                    continue;
                }
                String trimmed = name.trim();
                Marker marker = markerRepository.findByName(trimmed)
                        .orElseGet(() -> markerRepository.save(new Marker(null, trimmed)));
                ids.add(marker.getId());
            }
        }
        return ids;
    }

    private void validateMarkerIdsExist(final Set<Long> markerIds) {
        if (markerIds == null || markerIds.isEmpty()) {
            return;
        }
        for (Long markerId : markerIds) {
            if (!markerRepository.existsById(markerId)) {
                throw new ValidationException("Marker not found with id: " + markerId, MARKER_NOT_FOUND_CODE);
            }
        }
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }
}
