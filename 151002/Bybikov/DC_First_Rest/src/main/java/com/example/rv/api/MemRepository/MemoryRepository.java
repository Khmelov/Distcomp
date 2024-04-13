package com.example.rv.api.MemRepository;

import com.example.rv.api.repository.CrudRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryRepository <T> implements CrudRepository<T, Long> {

    protected final static AtomicLong ids = new AtomicLong();

    protected final Map<Long, T> map = new HashMap<>();

    @Override
    public Iterable<T> getAll(){
        return map.values();
    }

    @Override
    public Optional<T> getById(Long id){
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Optional<T> save(T entity) {
        return Optional.empty();
    }

    @Override
    public Optional<T> update(T entity) {
        return Optional.empty();
    }

    @Override
    public boolean delete(T entity) {
        return false;
    }


}
