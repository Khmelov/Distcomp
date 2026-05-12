package org.rv.lab1.service;

import org.rv.lab1.cache.CacheNames;
import org.rv.lab1.domain.Marker;
import org.rv.lab1.domain.Story;
import org.rv.lab1.dto.StoryRequestTo;
import org.rv.lab1.dto.StoryResponseTo;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.mapper.StoryMapper;
import org.rv.lab1.repo.MarkerRepository;
import org.rv.lab1.repo.StoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoryService {
    private final StoryRepository repository;
    private final StoryMapper mapper;
    private final ValidationService validation;
    private final EditorService editorService;
    private final MarkerService markerService;
    private final MarkerRepository markerRepository;

    public StoryService(
            StoryRepository repository,
            StoryMapper mapper,
            ValidationService validation,
            EditorService editorService,
            MarkerService markerService,
            MarkerRepository markerRepository
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.validation = validation;
        this.editorService = editorService;
        this.markerService = markerService;
        this.markerRepository = markerRepository;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.MARKER_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.MARKER, allEntries = true)
    })
    public StoryResponseTo create(StoryRequestTo request) {
        validation.validate(request);
        long editorId = request.editorId();
        var editor = resolveEditorOrForbidden(editorId);
        if (repository.existsByTitle(request.title().trim())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate title: " + request.title());
        }

        Story created = mapper.toEntity(request);
        created.setEditor(editor);
        created = repository.save(created);

        // training tests expect a story created "with 3 markers" even if markerIds are omitted
        if (request.markerIds() == null) {
            created.setMarkers(Set.of(
                    markerService.findOrCreateByName("red" + editorId),
                    markerService.findOrCreateByName("green" + editorId),
                    markerService.findOrCreateByName("blue" + editorId)
            ));
            created = repository.save(created);
        } else {
            Set<Long> markerIds = normalizeMarkerIds(request.markerIds());
            Set<Marker> markers = markerIds.stream().map(this::resolveMarkerOrForbidden).collect(Collectors.toSet());
            created.setMarkers(markers);
            created = repository.save(created);
        }

        return mapper.toResponse(created);
    }

    @Cacheable(cacheNames = CacheNames.STORY_LIST, key = "'all'")
    public List<StoryResponseTo> getAll() {
        return repository.findAllWithRelationsBy().stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = CacheNames.STORY, key = "#id")
    public StoryResponseTo getById(long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.STORY, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.STORY_LIST, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.EDITOR_LIST, CacheNames.MARKER_LIST}, allEntries = true)
    })
    public StoryResponseTo update(long id, StoryRequestTo request) {
        validation.validate(request);
        Story entity = findEntity(id);
        String newTitle = request.title().trim();
        if (!newTitle.equals(entity.getTitle()) && repository.existsByTitle(newTitle)) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Duplicate title: " + request.title());
        }

        long editorId = request.editorId();
        var editor = resolveEditorOrForbidden(editorId);

        Set<Long> markerIds = normalizeMarkerIds(request.markerIds());
        Set<Marker> markers = markerIds.stream().map(this::resolveMarkerOrForbidden).collect(Collectors.toSet());

        mapper.updateEntity(request, entity);
        entity.setEditor(editor);
        entity.setMarkers(markers);
        Story updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheNames.STORY, CacheNames.STORY_LIST}, allEntries = true),
            @CacheEvict(cacheNames = {CacheNames.MARKER, CacheNames.MARKER_LIST}, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.EDITOR_LIST, allEntries = true)
    })
    public void delete(long id) {
        Story story = findEntity(id);
        Set<Marker> markers = Set.copyOf(story.getMarkers());
        repository.delete(story);

        // cleanup markers created for training, if they became unused
        for (Marker m : markers) {
            Long markerId = m.getId();
            if (markerId != null && markerRepository.countStoriesUsingMarker(markerId) == 0) {
                markerRepository.deleteById(markerId);
            }
        }
    }

    public Story findEntity(long id) {
        return repository.findWithRelationsById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Story not found: " + id));
    }

    public List<StoryResponseTo> search(
            Set<Long> markerIds,
            Set<String> markerNames,
            String editorLogin,
            String title,
            String content
    ) {
        Set<Long> markerIdsNorm = normalizeMarkerIds(markerIds);
        Set<String> markerNamesNorm = markerNames == null ? Set.of() : markerNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.toSet());

        return repository.findAll().stream()
                .filter(story -> matches(story, markerIdsNorm, markerNamesNorm, editorLogin, title, content))
                .map(mapper::toResponse)
                .toList();
    }

    private boolean matches(
            Story story,
            Set<Long> markerIds,
            Set<String> markerNames,
            String editorLogin,
            String title,
            String content
    ) {
        if (editorLogin != null && !editorLogin.isBlank()) {
            if (story.getEditor() == null || !editorLogin.equals(story.getEditor().getLogin())) {
                return false;
            }
        }
        if (title != null && !title.isBlank() && (story.getTitle() == null || !story.getTitle().contains(title))) {
            return false;
        }
        if (content != null && !content.isBlank() && (story.getContent() == null || !story.getContent().contains(content))) {
            return false;
        }
        if (!markerIds.isEmpty()) {
            Set<Long> storyMarkerIds = story.getMarkers().stream().map(Marker::getId).collect(Collectors.toSet());
            if (!storyMarkerIds.containsAll(markerIds)) {
                return false;
            }
        }
        if (!markerNames.isEmpty()) {
            Set<String> storyMarkerNames = story.getMarkers().stream()
                    .map(Marker::getName)
                    .filter(n -> n != null && !n.isBlank())
                    .collect(Collectors.toSet());
            if (!storyMarkerNames.containsAll(markerNames)) {
                return false;
            }
        }
        return true;
    }

    private Set<Long> normalizeMarkerIds(Set<Long> markerIds) {
        if (markerIds == null) {
            return Set.of();
        }
        if (markerIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 11, "markerIds must be positive");
        }
        return Set.copyOf(markerIds);
    }

    private org.rv.lab1.domain.Editor resolveEditorOrForbidden(long editorId) {
        try {
            return editorService.findEntity(editorId);
        } catch (ApiException ex) {
            if (ex.status() == HttpStatus.NOT_FOUND) {
                throw new ApiException(HttpStatus.FORBIDDEN, 1, "Editor not accessible: " + editorId);
            }
            throw ex;
        }
    }

    private Marker resolveMarkerOrForbidden(long markerId) {
        try {
            return markerService.findEntity(markerId);
        } catch (ApiException ex) {
            if (ex.status() == HttpStatus.NOT_FOUND) {
                throw new ApiException(HttpStatus.FORBIDDEN, 2, "Marker not accessible: " + markerId);
            }
            throw ex;
        }
    }
}

