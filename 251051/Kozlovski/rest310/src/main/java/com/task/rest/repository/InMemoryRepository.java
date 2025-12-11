package com.task.rest.repository;

import com.task.rest.model.BaseEntity;
import com.task.rest.model.Comment;
import com.task.rest.model.Tweet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class InMemoryRepository<T extends BaseEntity> implements GenericRepository<T> {
    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public List<T> findByWriterId(Long writerId) {
        return storage.values().stream()
                .filter(entity -> entity instanceof Tweet && ((Tweet) entity).getWriterId().equals(writerId))
                .toList();
    }

    public List<T> findByTweetId(Long tweetId) {
        return storage.values().stream()
                .filter(entity -> {
                    if (entity instanceof Comment) return ((Comment) entity).getTweetId().equals(tweetId);
                    if (entity instanceof Tweet) return ((Tweet) entity).getId().equals(tweetId);
                    return false;
                })
                .toList();
    }

    public List<T> findByMarkId(Long markId) {
        return storage.values().stream()
                .filter(entity -> entity instanceof Tweet && ((Tweet) entity).getMarks().stream()
                        .anyMatch(mark -> mark.getId().equals(markId)))
                .toList();
    }
}