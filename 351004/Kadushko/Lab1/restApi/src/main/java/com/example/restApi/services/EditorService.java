package com.example.restApi.services;

import com.example.restApi.dto.request.EditorRequestTo;
import com.example.restApi.dto.response.EditorResponseTo;
import com.example.restApi.exception.AlreadyExistsException;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.EditorMapper;
import com.example.restApi.model.Editor;
import com.example.restApi.repository.EditorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorService {

    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper;

    public EditorService(EditorRepository editorRepository, EditorMapper editorMapper) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
    }

    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toDto)
                .collect(Collectors.toList());
    }

    public EditorResponseTo getById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id));
        return editorMapper.toDto(editor);
    }

    public EditorResponseTo create(EditorRequestTo request) {
        if (editorRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("Editor with login '" + request.getLogin() + "' already exists");
        }
        Editor editor = editorMapper.toEntity(request);
        editor.setId(null);
        Editor saved = editorRepository.save(editor);
        return editorMapper.toDto(saved);
    }

    public EditorResponseTo update(EditorRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Editor ID is required for update");
        }
        Editor editor = editorRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + request.getId()));
        if (editorRepository.existsByLoginAndIdNot(request.getLogin(), request.getId())) {
            throw new AlreadyExistsException("Editor with login '" + request.getLogin() + "' already exists");
        }
        editorMapper.updateEntity(editor, request);
        Editor updated = editorRepository.save(editor);
        return editorMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new NotFoundException("Editor not found with id: " + id);
        }
        editorRepository.deleteById(id);
    }
}
