package com.sergey.orsik.repository.impl;

import com.sergey.orsik.entity.Comment;
import com.sergey.orsik.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCommentRepository implements CrudRepository<Comment> {

    private final Map<Long, Comment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Comment save(Comment entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), new Comment(
                entity.getId(),
                entity.getTweetId(),
                entity.getContent()
        ));
        return entity;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Comment> findAll() {
        return storage.values().stream()
                .map(c -> new Comment(
                        c.getId(),
                        c.getTweetId(),
                        c.getContent()
                ))
                .toList();
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
