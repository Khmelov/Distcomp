package by.tracker.rest_api.repository;

import by.tracker.rest_api.model.Creator;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCreatorRepository implements CrudRepository<Creator, Long> {

    private final Map<Long, Creator> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Creator save(Creator entity) {
        entity.setId(idGenerator.getAndIncrement());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Creator> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Creator> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Creator update(Creator entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Creator not found with id: " + entity.getId());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        if (!storage.containsKey(id)) {
            throw new NoSuchElementException("Creator not found with id: " + id);
        }
        storage.remove(id);
    }
}