package by.yelkin.TopicService.repository;

import by.yelkin.TopicService.entity.BaseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CrudRepository<T extends BaseEntity> {
    private Long sequence = 0L;
    private final Map<Long, T> entities = new ConcurrentHashMap<>();

    public T save(T t) {
        if (t.getId() == null) {
            entities.put(sequence, t);
            t.setId(sequence++);
        } else {
            entities.put(t.getId(), t);
        }

        return t;
    }

    public List<T> findAll() {
        return entities.values().stream().toList();
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    public void deleteById(Long id) {
        entities.remove(id);
    }
}
