package com.blog.repository.impl;

import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEditorRepository implements EditorRepository {
    private final Map<Long, Editor> editors = new HashMap<>();
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
            throw new IllegalArgumentException("Editor not found");
        }
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

    @Override
    public Optional<Editor> findByLogin(String login) {
        return editors.values().stream()
                .filter(editor -> editor.getLogin().equals(login))
                .findFirst();
    }
}