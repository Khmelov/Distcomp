package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.WriterRepository;
import com.lizaveta.notebook.repository.entity.WriterEntity;
import com.lizaveta.notebook.repository.jpa.WriterJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class WriterJpaRepositoryAdapter implements WriterRepository {

    private final WriterJpaRepository jpaRepository;

    public WriterJpaRepositoryAdapter(final WriterJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Writer> findById(final Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Writer> findAll() {
        return StreamSupport.stream(jpaRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<Writer> findAll(final Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Writer save(final Writer entity) {
        WriterEntity e = toEntity(entity);
        WriterEntity saved = jpaRepository.save(e);
        return toDomain(saved);
    }

    @Override
    public Writer update(final Writer entity) {
        WriterEntity e = toEntity(entity);
        e.setId(entity.getId());
        WriterEntity saved = jpaRepository.save(e);
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
    public boolean existsByLogin(final String login) {
        return jpaRepository.existsByLogin(login);
    }

    private Writer toDomain(final WriterEntity e) {
        return new Writer(e.getId(), e.getLogin(), e.getPassword(), e.getFirstname(), e.getLastname());
    }

    private WriterEntity toEntity(final Writer w) {
        WriterEntity e = new WriterEntity();
        e.setId(w.getId());
        e.setLogin(w.getLogin());
        e.setPassword(w.getPassword());
        e.setFirstname(w.getFirstname());
        e.setLastname(w.getLastname());
        return e;
    }
}
