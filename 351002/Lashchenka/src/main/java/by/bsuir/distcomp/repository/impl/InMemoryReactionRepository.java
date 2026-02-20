package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReactionRepository implements CrudRepository<Reaction, Long> {

    private final Map<Long, Reaction> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Reaction save(Reaction entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
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
}
