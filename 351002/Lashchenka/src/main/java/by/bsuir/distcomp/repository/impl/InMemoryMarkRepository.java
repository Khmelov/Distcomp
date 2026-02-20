package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Mark;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMarkRepository implements CrudRepository<Mark, Long> {

    private final Map<Long, Mark> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Mark save(Mark entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Mark> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Mark> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Mark update(Mark entity) {
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

    public boolean existsByName(String name) {
        return storage.values().stream()
                .anyMatch(mark -> mark.getName().equals(name));
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return storage.values().stream()
                .anyMatch(mark -> mark.getName().equals(name) && !mark.getId().equals(id));
    }
}
