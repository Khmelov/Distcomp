package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityNotFoundException;
import org.example.model.Editor;
import org.example.repository.EditorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;

    public Editor create(Editor editor) {
        // Так как автоинкремента нет, ID должен приходить либо извне,
        // либо генерироваться (например, через System.currentTimeMillis())
        if (editor.getId() == null) {
            editor.setId(System.currentTimeMillis());
        }
        return repository.save(editor);
    }

    public List<Editor> findAll() {
        return repository.findAll();
    }

    public Editor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Editor not found"));
    }

    public Editor update(Editor editor) {
        if (!repository.existsById(editor.getId())) {
            throw new EntityNotFoundException("Editor not found");
        }
        return repository.save(editor);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Editor not found");
        }
        repository.deleteById(id);
    }
}