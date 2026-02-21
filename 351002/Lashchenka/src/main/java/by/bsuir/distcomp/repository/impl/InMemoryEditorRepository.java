package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Editor;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEditorRepository implements CrudRepository<Editor, Long> {

    private final Map<Long, Editor> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Editor save(Editor entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Editor> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Editor> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Editor update(Editor entity) {
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

    public boolean existsByLogin(String login) {
        return storage.values().stream()
                .anyMatch(editor -> editor.getLogin().equals(login));
    }

    public boolean existsByLoginAndIdNot(String login, Long id) {
        return storage.values().stream()
                .anyMatch(editor -> editor.getLogin().equals(login) && !editor.getId().equals(id));
    }
}
