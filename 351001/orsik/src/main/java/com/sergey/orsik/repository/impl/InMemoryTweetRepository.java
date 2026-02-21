package com.sergey.orsik.repository.impl;

import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTweetRepository implements CrudRepository<Tweet> {

    private final Map<Long, Tweet> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Tweet save(Tweet entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), new Tweet(
                entity.getId(),
                entity.getCreatorId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreated(),
                entity.getModified()
        ));
        return entity;
    }

    @Override
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Tweet> findAll() {
        return storage.values().stream()
                .map(t -> new Tweet(
                        t.getId(),
                        t.getCreatorId(),
                        t.getTitle(),
                        t.getContent(),
                        t.getCreated(),
                        t.getModified()
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
