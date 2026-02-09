package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.mapper.StoryMapper;
import com.lizaveta.notebook.model.dto.request.StoryRequestTo;
import com.lizaveta.notebook.model.dto.response.StoryResponseTo;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.MarkerRepository;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Service for Story CRUD operations.
 */
@Service
public class StoryService {

    private static final String STORY_NOT_FOUND = "Story not found with id: ";
    private static final int WRITER_NOT_FOUND_CODE = 40001;
    private static final int MARKER_NOT_FOUND_CODE = 40003;

    private final StoryRepository repository;
    private final WriterRepository writerRepository;
    private final MarkerRepository markerRepository;
    private final StoryMapper mapper;

    public StoryService(
            final StoryRepository repository,
            final WriterRepository writerRepository,
            final MarkerRepository markerRepository,
            final StoryMapper mapper) {
        this.repository = repository;
        this.writerRepository = writerRepository;
        this.markerRepository = markerRepository;
        this.mapper = mapper;
    }

    public StoryResponseTo create(final StoryRequestTo request) {
        validateWriterExists(request.writerId());
        validateMarkerIdsExist(request.markerIds());
        Story entity = mapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        Story withTimestamps = new Story(
                null, entity.getWriterId(), entity.getTitle(), entity.getContent(),
                now, now, entity.getMarkerIds());
        Story saved = repository.save(withTimestamps);
        return mapper.toResponse(saved);
    }

    public List<StoryResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public StoryResponseTo findById(final Long id) {
        Story entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STORY_NOT_FOUND + id));
        return mapper.toResponse(entity);
    }

    public StoryResponseTo update(final Long id, final StoryRequestTo request) {
        Story existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STORY_NOT_FOUND + id));
        validateWriterExists(request.writerId());
        validateMarkerIdsExist(request.markerIds());
        Story updated = existing.withWriterId(request.writerId())
                .withTitle(request.title())
                .withContent(request.content())
                .withMarkerIds(request.markerIds() != null ? Set.copyOf(request.markerIds()) : Set.of())
                .withModified(LocalDateTime.now());
        repository.update(updated);
        return mapper.toResponse(updated);
    }

    public void deleteById(final Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException(STORY_NOT_FOUND + id);
        }
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
}
