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
import java.util.Optional;

@Service
@Transactional
public class EditorService {
    
    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper;
    
    public EditorService(EditorRepository editorRepository, EditorMapper editorMapper) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
    }
    
    public EditorResponseTo create(EditorRequestTo request) {
        validateEditorRequest(request);
        
        // Проверка уникальности логина
        Optional<Editor> existingEditor = editorRepository.findByLogin(request.getLogin());
        if (existingEditor.isPresent()) {
            throw new ApiException(400, "40010", "Editor with login '" + request.getLogin() + "' already exists");
        }
        
        Editor editor = editorMapper.toEntity(request);
        editor = editorRepository.save(editor);
        return editorMapper.toResponse(editor);
    }
    
    @Transactional(readOnly = true)
    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + id));
        return editorMapper.toResponse(editor);
    }
    
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        validateEditorRequest(request);
        
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Editor not found with id: " + id));
        
        // Проверка уникальности логина (кроме текущего редактора)
        if (!editor.getLogin().equals(request.getLogin())) {
            Optional<Editor> existingEditor = editorRepository.findByLogin(request.getLogin());
            if (existingEditor.isPresent()) {
                throw new ApiException(400, "40010", "Editor with login '" + request.getLogin() + "' already exists");
            }
        }
        
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());
        
        editor = editorRepository.save(editor);
        return editorMapper.toResponse(editor);
    }
    
    public void delete(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new ApiException(404, "40401", "Editor not found with id: " + id);
        }
        editorRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<EditorResponseTo> findByLogin(String login) {
        return editorRepository.findByLogin(login)
                .map(editorMapper::toResponse);
    }
    
    private void validateEditorRequest(EditorRequestTo request) {
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ApiException(400, "40011", "Login cannot be empty");
        }
        if (request.getLogin().length() < 2 || request.getLogin().length() > 64) {
            throw new ApiException(400, "40012", "Login must be between 2 and 64 characters");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ApiException(400, "40013", "Password cannot be empty");
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            throw new ApiException(400, "40014", "Password must be between 8 and 128 characters");
        }
        
        if (request.getFirstname() == null || request.getFirstname().trim().isEmpty()) {
            throw new ApiException(400, "40015", "Firstname cannot be empty");
        }
        if (request.getFirstname().length() < 2 || request.getFirstname().length() > 64) {
            throw new ApiException(400, "40016", "Firstname must be between 2 and 64 characters");
        }
        
        if (request.getLastname() == null || request.getLastname().trim().isEmpty()) {
            throw new ApiException(400, "40017", "Lastname cannot be empty");
        }
        if (request.getLastname().length() < 2 || request.getLastname().length() > 64) {
            throw new ApiException(400, "40018", "Lastname must be between 2 and 64 characters");
        }
    }
}