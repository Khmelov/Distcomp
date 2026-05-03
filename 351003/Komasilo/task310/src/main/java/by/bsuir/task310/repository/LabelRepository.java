package by.bsuir.task310.repository;

import by.bsuir.task310.model.Label;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LabelRepository {
    private final ConcurrentHashMap<Long, Label> labels = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Label save(Label label) {
        long id = idGenerator.incrementAndGet();
        label.setId(id);
        labels.put(id, label);
        return label;
    }

    public List<Label> findAll() {
        return new ArrayList<>(labels.values());
    }

    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(labels.get(id));
    }

    public Label update(Label label) {
        labels.put(label.getId(), label);
        return label;
    }

    public boolean deleteById(Long id) {
        return labels.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return labels.containsKey(id);
    }
}