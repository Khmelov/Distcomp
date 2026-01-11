package by.rest.service;

import by.rest.domain.Editor;
import by.rest.dto.EditorRequestTo;
import by.rest.dto.EditorResponseTo;
import by.rest.exception.ApiException;
import by.rest.mapper.EditorMapper;
import by.rest.repository.EditorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EditorService {
    private final EditorRepository repo;
    private final EditorMapper mapper;

    public EditorService(EditorRepository repo, EditorMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public EditorResponseTo create(EditorRequestTo req) {
        // Упрощенная валидация
        if (req.getLogin() == null || req.getLogin().trim().isEmpty()) {
            throw new ApiException(400, "40001", "Login is required");
        }
        
        Editor editor = mapper.toEntity(req);
        editor = repo.save(editor);
        return mapper.toResponse(editor);
    }

    @Transactional(readOnly = true)
    public List<EditorResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        Editor editor = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
        return mapper.toResponse(editor);
    }

    public EditorResponseTo update(Long id, EditorRequestTo req) {
        // Упрощенная валидация
        if (req.getLogin() == null || req.getLogin().trim().isEmpty()) {
            throw new ApiException(400, "40001", "Login is required");
        }
        
        Editor editor = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
        
        // Обновляем поля напрямую
        editor.setLogin(req.getLogin());
        editor.setPassword(req.getPassword());
        editor.setFirstname(req.getFirstname());
        editor.setLastname(req.getLastname());
        
        editor = repo.save(editor);
        return mapper.toResponse(editor);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(404, "40401", "Editor not found");
        }
        repo.deleteById(id);
    }
}