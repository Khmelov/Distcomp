package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Editor;
import org.example.repository.EditorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;

    public Editor create(Editor editor) {
        // Используем findByLogin вместо existsByLogin
        var existingEditors = repository.findByLogin(editor.getLogin());

        if (!existingEditors.isEmpty()) {
            // ТЕСТ ЖДЕТ 403 (FORBIDDEN)
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Login already exists"
            );
        }

        if (editor.getId() == null) {
            editor.setId(java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        }
        return repository.save(editor);
    }

    public List<Editor> findAll() {
        return repository.findAll();
    }

    public Editor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Editor not found"));
    }

    public Editor update(Editor editor) {
        if (!repository.existsById(editor.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Editor not found");
        }
        return repository.save(editor);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Editor not found");
        }
        repository.deleteById(id);
    }
}