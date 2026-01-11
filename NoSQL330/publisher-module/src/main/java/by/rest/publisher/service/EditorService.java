package by.rest.publisher.service;

import by.rest.publisher.domain.Editor;
import by.rest.publisher.dto.EditorRequestTo;
import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.mapper.EditorMapper;
import by.rest.publisher.repository.EditorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        validate(req);
        Editor editor = mapper.toEntity(req);
        editor = repo.save(editor);
        return mapper.toResponse(editor);
    }
    
    @Transactional(readOnly = true)
    public List<EditorResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        Editor editor = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
        return mapper.toResponse(editor);
    }
    
    public EditorResponseTo update(Long id, EditorRequestTo req) {
        validate(req);
        Editor editor = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found"));
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
    
    private void validate(EditorRequestTo req) {
        if (req.getLogin() == null || req.getLogin().length() < 2 || req.getLogin().length() > 64) {
            throw new ApiException(400, "40002", "Login must be between 2 and 64 characters");
        }
        if (req.getPassword() == null || req.getPassword().length() < 8 || req.getPassword().length() > 128) {
            throw new ApiException(400, "40002", "Password must be between 8 and 128 characters");
        }
        if (req.getFirstname() == null || req.getFirstname().length() < 2 || req.getFirstname().length() > 64) {
            throw new ApiException(400, "40002", "Firstname must be between 2 and 64 characters");
        }
        if (req.getLastname() == null || req.getLastname().length() < 2 || req.getLastname().length() > 64) {
            throw new ApiException(400, "40002", "Lastname must be between 2 and 64 characters");
        }
    }
}