package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import java.util.List;

@Profile("docker")
public abstract class AbstractGenericService<T, ID> implements GenericService<T, ID> {
    protected final JpaRepository<T, ID> repository;
    protected final JpaSpecificationExecutor<T> specExecutor;

    public AbstractGenericService(JpaRepository<T, ID> repository, JpaSpecificationExecutor<T> specExecutor) {
        this.repository = repository;
        this.specExecutor = specExecutor;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return specExecutor.findAll(spec, pageable);
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return specExecutor.findAll(spec, sort);
    }
}