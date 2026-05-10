package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.repository.entity.MarkerEntity;
import com.lizaveta.notebook.repository.entity.StoryEntity;
import com.lizaveta.notebook.repository.jpa.MarkerJpaRepository;
import com.lizaveta.notebook.repository.jpa.StoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

@Repository
public class StoryJpaRepositoryAdapter implements StoryRepository {

    private final StoryJpaRepository jpaRepository;
    private final MarkerJpaRepository markerJpaRepository;

    public StoryJpaRepositoryAdapter(
            final StoryJpaRepository jpaRepository,
            final MarkerJpaRepository markerJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.markerJpaRepository = markerJpaRepository;
    }

    @Override
    public Optional<Story> findById(final Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Story> findAll() {
        return StreamSupport.stream(jpaRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Story> findAll(final Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Story save(final Story entity) {
        StoryEntity e = toEntity(entity);
        StoryEntity saved = jpaRepository.save(e);
        return toDomain(saved);
    }

    @Override
    public Story update(final Story entity) {
        StoryEntity e = toEntity(entity);
        e.setId(entity.getId());
        StoryEntity saved = jpaRepository.save(e);
        return toDomain(saved);
    }

    @Override
    public boolean deleteById(final Long id) {
        if (!jpaRepository.existsById(id)) {
            return false;
        }
        jpaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existsById(final Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Story> findByMarkerId(final Long markerId) {
        return jpaRepository.findByMarkerId(markerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByWriterIdAndTitle(final Long writerId, final String title) {
        return jpaRepository.existsByWriterIdAndTitle(writerId, title);
    }

    @Override
    public boolean existsByWriterIdAndTitleAndIdNot(final Long writerId, final String title, final Long excludeId) {
        return jpaRepository.existsByWriterIdAndTitleAndIdNot(writerId, title, excludeId);
    }

    private Story toDomain(final StoryEntity e) {
        Set<Long> markerIds = e.getMarkers().stream()
                .map(MarkerEntity::getId)
                .collect(java.util.stream.Collectors.toSet());
        return new Story(
                e.getId(),
                e.getWriterId(),
                e.getTitle(),
                e.getContent(),
                e.getCreated(),
                e.getModified(),
                markerIds);
    }

    private StoryEntity toEntity(final Story s) {
        StoryEntity e = new StoryEntity();
        e.setId(s.getId());
        e.setWriterId(s.getWriterId());
        e.setTitle(s.getTitle());
        e.setContent(s.getContent());
        e.setCreated(s.getCreated());
        e.setModified(s.getModified());
        Set<MarkerEntity> markers = new HashSet<>();
        if (s.getMarkerIds() != null && !s.getMarkerIds().isEmpty()) {
            markers = new HashSet<>(markerJpaRepository.findAllById(s.getMarkerIds()));
        }
        e.setMarkers(markers);
        return e;
    }
}
