package com.example.storyapp.repository;

import com.example.storyapp.model.Label;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLabelRepository implements CrudRepository<Label, Long> {
    private final Map<Long, Label> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Label> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Label save(Label label) {
        if (label.getId() == null) {
            label.setId(idGenerator.getAndIncrement());
        }
        store.put(label.getId(), label);
        return label;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public Label findByName(String name) {
        return store.values().stream()
                .filter(label -> label.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Label> findAllByNameIn(List<String> names) {
        return store.values().stream()
                .filter(label -> names.contains(label.getName()))
                .toList();
    }
}