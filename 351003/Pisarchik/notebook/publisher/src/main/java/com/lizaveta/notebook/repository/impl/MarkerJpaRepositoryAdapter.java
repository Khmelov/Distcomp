package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Marker;
import com.lizaveta.notebook.repository.MarkerRepository;
import com.lizaveta.notebook.repository.entity.MarkerEntity;
import com.lizaveta.notebook.repository.jpa.MarkerJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class MarkerJpaRepositoryAdapter implements MarkerRepository {

    private final MarkerJpaRepository jpaRepository;

    public MarkerJpaRepositoryAdapter(final MarkerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Marker> findById(final Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Marker> findAll() {
        return StreamSupport.stream(jpaRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Marker> findAll(final Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Marker save(final Marker entity) {
        MarkerEntity e = toEntity(entity);
        MarkerEntity saved = jpaRepository.save(e);
        return toDomain(saved);
    }

    @Override
    public Marker update(final Marker entity) {
        MarkerEntity e = toEntity(entity);
        e.setId(entity.getId());
        MarkerEntity saved = jpaRepository.save(e);
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
    public Optional<Marker> findByName(final String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    private Marker toDomain(final MarkerEntity e) {
        return new Marker(e.getId(), e.getName());
    }

    private MarkerEntity toEntity(final Marker m) {
        MarkerEntity e = new MarkerEntity();
        e.setId(m.getId());
        e.setName(m.getName());
        return e;
    }
}
