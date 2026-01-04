package com.task310.socialnetwork.repository.impl;

import com.task310.socialnetwork.model.Label;
import com.task310.socialnetwork.repository.LabelRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLabelRepository implements LabelRepository {
    private final Map<Long, Label> labels = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Label> findAll() {
        return new ArrayList<>(labels.values());
    }

    @Override
    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(labels.get(id));
    }

    @Override
    public Label save(Label label) {
        if (label.getId() == null) {
            label.setId(idCounter.getAndIncrement());
        }
        labels.put(label.getId(), label);
        return label;
    }

    @Override
    public Label update(Label label) {
        if (label.getId() == null || !labels.containsKey(label.getId())) {
            throw new IllegalArgumentException("Label not found");
        }
        labels.put(label.getId(), label);
        return label;
    }

    @Override
    public boolean deleteById(Long id) {
        return labels.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return labels.containsKey(id);
    }

    @Override
    public Optional<Label> findByName(String name) {
        return labels.values().stream()
                .filter(label -> label.getName().equals(name))
                .findFirst();
    }
}