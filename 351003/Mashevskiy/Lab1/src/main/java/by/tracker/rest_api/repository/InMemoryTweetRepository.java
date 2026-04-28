package by.tracker.rest_api.repository;

import by.tracker.rest_api.model.Tweet;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTweetRepository implements CrudRepository<Tweet, Long> {

    private final Map<Long, Tweet> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Tweet save(Tweet entity) {
        entity.setId(idGenerator.getAndIncrement());
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Tweet> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Tweet update(Tweet entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Tweet not found with id: " + entity.getId());
        }
        Tweet existing = storage.get(entity.getId());
        entity.setCreated(existing.getCreated());
        entity.setModified(LocalDateTime.now());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        if (!storage.containsKey(id)) {
            throw new NoSuchElementException("Tweet not found with id: " + id);
        }
        storage.remove(id);
    }
}