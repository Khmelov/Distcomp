package com.example.app.repository;

import org.springframework.stereotype.Repository;

import com.example.app.model.Reaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryReactionRepository implements CrudRepository<Reaction, Long> {
    private final Map<Long, Reaction> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Reaction> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Reaction> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Reaction save(Reaction reaction) {
        if (reaction.getId() == null) {
            reaction.setId(idGenerator.getAndIncrement());
        }
        store.put(reaction.getId(), reaction);
        return reaction;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public List<Reaction> findByTweetId(Long tweetId) {
        return store.values().stream()
                .filter(reaction -> tweetId.equals(reaction.getTweetId()))
                .collect(Collectors.toList());
    }
}