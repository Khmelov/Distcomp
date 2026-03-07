package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Notice;
import com.lizaveta.notebook.repository.NoticeRepository;
import com.lizaveta.notebook.repository.entity.NoticeEntity;
import com.lizaveta.notebook.repository.jpa.NoticeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class NoticeJpaRepositoryAdapter implements NoticeRepository {

    private final NoticeJpaRepository jpaRepository;

    public NoticeJpaRepositoryAdapter(final NoticeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Notice> findById(final Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notice> findAll() {
        return StreamSupport.stream(jpaRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Notice> findAll(final Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Notice save(final Notice entity) {
        NoticeEntity e = toEntity(entity);
        NoticeEntity saved = jpaRepository.save(e);
        return toDomain(saved);
    }

    @Override
    public Notice update(final Notice entity) {
        NoticeEntity e = toEntity(entity);
        e.setId(entity.getId());
        NoticeEntity saved = jpaRepository.save(e);
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
    public List<Notice> findByStoryId(final Long storyId) {
        return jpaRepository.findByStoryId(storyId).stream()
                .map(this::toDomain)
                .toList();
    }

    private Notice toDomain(final NoticeEntity e) {
        return new Notice(e.getId(), e.getStoryId(), e.getContent());
    }

    private NoticeEntity toEntity(final Notice n) {
        NoticeEntity e = new NoticeEntity();
        e.setId(n.getId());
        e.setStoryId(n.getStoryId());
        e.setContent(n.getContent());
        return e;
    }
}
