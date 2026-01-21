// InMemoryEditorRepository.java
package com.example.publisher.repository;

import org.springframework.stereotype.Repository;
import com.example.publisher.model.Editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEditorRepository implements CrudRepository<Editor, Long> {
    private final Map<Long, Editor> editors = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Editor> findAll() {
        return new ArrayList<>(editors.values());
    }

    @Override
    public Optional<Editor> findById(Long id) {
        return Optional.ofNullable(editors.get(id));
    }

    @Override
    public Editor save(Editor editor) {
        if (editor.getId() == null) {
            editor.setId(idCounter.getAndIncrement());
        }
        editors.put(editor.getId(), editor);
        return editor;
    }

    @Override
    public Editor update(Editor editor) {
        if (editor.getId() == null || !editors.containsKey(editor.getId())) {
            throw new IllegalArgumentException("Editor not found with id: " + editor.getId());
        }
        editor.setModified(java.time.LocalDateTime.now());
        editors.put(editor.getId(), editor);
        return editor;
    }

    @Override
    public boolean deleteById(Long id) {
        return editors.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return editors.containsKey(id);
    }

    public Optional<Editor> findByLogin(String login) {
        return editors.values().stream()
                .filter(editor -> editor.getLogin().equals(login))
                .findFirst();
    }
}