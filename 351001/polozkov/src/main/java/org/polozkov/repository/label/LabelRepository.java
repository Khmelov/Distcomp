package org.polozkov.repository.label;

import org.polozkov.entity.label.Label;
import org.polozkov.exception.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LabelRepository {

    private final ConcurrentHashMap<Long, Label> labels = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Label> findAll() {
        return new ArrayList<>(labels.values());
    }

    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(labels.get(id));
    }

    public Label getById(Long id) {
        return findById(id).orElseThrow(() ->
                new NotFoundException("Label not found with id: " + id));
    }

    public Label save(Label label) {
        if (label.getId() == null) {
            label.setId(idCounter.getAndIncrement());
        }
        labels.put(label.getId(), label);
        return label;
    }

    public Label update(Label label) {
        if (!labels.containsKey(label.getId())) {
            throw new RuntimeException("Label not found with id: " + label.getId());
        }
        labels.put(label.getId(), label);
        return label;
    }

    public void deleteById(Long id) {
        labels.remove(id);
    }

    public boolean existsById(Long id) {
        return labels.containsKey(id);
    }

    public Optional<Label> findByName(String name) {
        return labels.values().stream()
                .filter(label -> label.getName().equals(name))
                .findFirst();
    }
}