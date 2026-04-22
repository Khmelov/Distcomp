package by.tracker.rest_api.repository;

import by.tracker.rest_api.model.Note;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryNoteRepository implements CrudRepository<Note, Long> {

    private final Map<Long, Note> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Note save(Note entity) {
        entity.setId(idGenerator.getAndIncrement());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Note> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Note update(Note entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new NoSuchElementException("Note not found with id: " + entity.getId());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        if (!storage.containsKey(id)) {
            throw new NoSuchElementException("Note not found with id: " + id);
        }
        storage.remove(id);
    }
}