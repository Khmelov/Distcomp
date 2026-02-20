package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryReactionRepository implements CrudRepository<Reaction, Long> {
    private final Map<Long, Reaction> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Reaction save(Reaction entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Reaction> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Reaction> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Reaction update(Reaction entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity with id " + entity.getId() + " does not exist");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public List<Reaction> findByTweetId(Long tweetId) {
        return storage.values().stream()
                .filter(reaction -> tweetId.equals(reaction.getTweetId()))
                .collect(Collectors.toList());
    }
}
