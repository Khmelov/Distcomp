package com.example.app.repository;

import org.springframework.stereotype.Repository;

import com.example.app.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTagRepository implements CrudRepository<Tag, Long> {
    private final Map<Long, Tag> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Tag> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            tag.setId(idGenerator.getAndIncrement());
        }
        store.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public Tag findByName(String name) {
        return store.values().stream()
                .filter(tag -> tag.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Tag> findAllByNameIn(List<String> names) {
        return store.values().stream()
                .filter(tag -> names.contains(tag.getName()))
                .toList();
    }
}