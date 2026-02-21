package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTweetRepository implements CrudRepository<Tweet, Long> {

    private final Map<Long, Tweet> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Tweet save(Tweet entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
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
        if (storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
            return entity;
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public boolean existsByTitle(String title) {
        return storage.values().stream()
                .anyMatch(tweet -> tweet.getTitle().equals(title));
    }

    public boolean existsByTitleAndIdNot(String title, Long id) {
        return storage.values().stream()
                .anyMatch(tweet -> tweet.getTitle().equals(title) && !tweet.getId().equals(id));
    }
}
