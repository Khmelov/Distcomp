package com.lizaveta.discussion.service;

import com.lizaveta.discussion.cassandra.NoticeByIdRow;
import com.lizaveta.discussion.exception.ResourceNotFoundException;
import com.lizaveta.discussion.exception.ValidationException;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private static final String NOTICE_NOT_FOUND = "Notice not found with id: ";
    private static final int INVALID_ID_CODE = 40004;

    private final NoticePersistenceService persistence;
    private final NoticeIdGenerator idGenerator;

    public NoticeService(final NoticePersistenceService persistence, final NoticeIdGenerator idGenerator) {
        this.persistence = persistence;
        this.idGenerator = idGenerator;
    }

    /**
     * Persists a new notice and returns its representation.
     */
    public NoticeResponseTo create(final NoticeRequestTo request) {
        long id = idGenerator.nextId();
        persistence.insert(id, request.storyId(), request.content());
        return new NoticeResponseTo(id, request.storyId(), request.content());
    }

    /**
     * @return all notices (ordered by id) — acceptable for moderate data volumes; Cassandra scans all partitions.
     */
    public List<NoticeResponseTo> findAll() {
        return persistence.findAllByIdTable().stream()
                .sorted(Comparator.comparingLong(NoticeByIdRow::getId))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Page of notices with optional in-memory sort (Cassandra has no global secondary ordering).
     */
    public PageResponseTo<NoticeResponseTo> findAll(final int page, final int size, final String sortBy, final String sortOrder) {
        List<NoticeByIdRow> all = new ArrayList<>(persistence.findAllByIdTable());
        Comparator<NoticeByIdRow> comparator = resolveComparator(sortBy, sortOrder);
        all.sort(comparator);
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        List<NoticeResponseTo> slice = all.subList(from, to).stream().map(this::toResponse).toList();
        int totalPages = safeSize > 0 ? (int) Math.ceil((double) all.size() / safeSize) : 0;
        return new PageResponseTo<>(slice, all.size(), totalPages, safeSize, safePage);
    }

    /**
     * @return notice by global id
     */
    public NoticeResponseTo findById(final Long id) {
        validateId(id);
        NoticeByIdRow row = persistence.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTICE_NOT_FOUND + id));
        return toResponse(row);
    }

    /**
     * @return notices for one story, ordered by id
     */
    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        validateId(storyId);
        return persistence.findByStoryId(storyId).stream()
                .sorted(Comparator.comparingLong(r -> r.getKey().getId()))
                .map(r -> new NoticeResponseTo(r.getKey().getId(), r.getKey().getStoryId(), r.getContent()))
                .toList();
    }

    /**
     * Updates content and optional story move.
     */
    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        validateId(id);
        NoticeByIdRow existing = persistence.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTICE_NOT_FOUND + id));
        long oldStoryId = existing.getStoryId();
        persistence.update(id, oldStoryId, request.storyId(), request.content());
        return new NoticeResponseTo(id, request.storyId(), request.content());
    }

    /**
     * Deletes a notice if it exists.
     */
    public void deleteById(final Long id) {
        validateId(id);
        Optional<NoticeByIdRow> row = persistence.findById(id);
        if (row.isEmpty()) {
            throw new ResourceNotFoundException(NOTICE_NOT_FOUND + id);
        }
        persistence.delete(id, row.get().getStoryId());
    }

    private NoticeResponseTo toResponse(final NoticeByIdRow row) {
        return new NoticeResponseTo(row.getId(), row.getStoryId(), row.getContent());
    }

    private Comparator<NoticeByIdRow> resolveComparator(final String sortBy, final String sortOrder) {
        boolean desc = sortOrder != null && sortOrder.equalsIgnoreCase("desc");
        Comparator<NoticeByIdRow> base;
        if (sortBy != null && !sortBy.isBlank()) {
            if ("storyId".equalsIgnoreCase(sortBy)) {
                base = Comparator.comparingLong(NoticeByIdRow::getStoryId);
            } else if ("content".equalsIgnoreCase(sortBy)) {
                base = Comparator.comparing(NoticeByIdRow::getContent, Comparator.nullsFirst(String::compareTo));
            } else {
                base = Comparator.comparingLong(NoticeByIdRow::getId);
            }
        } else {
            base = Comparator.comparingLong(NoticeByIdRow::getId);
        }
        return desc ? base.reversed() : base;
    }

    private void validateId(final Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number", INVALID_ID_CODE);
        }
    }
}
