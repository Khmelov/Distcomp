package com.blog.service.impl;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.mapper.EditorMapper;
import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import com.blog.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorServiceImpl implements EditorService {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Override
    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EditorResponseTo getById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editor not found with id: " + id));
        return editorMapper.toResponse(editor);
    }

    @Override
    public EditorResponseTo create(EditorRequestTo request) {
        // Валидация будет выполняться в контроллере
        Editor editor = editorMapper.toEntity(request);
        Editor saved = editorRepository.save(editor);
        return editorMapper.toResponse(saved);
    }

    @Override
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor existingEditor = editorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editor not found with id: " + id));

        Editor editor = editorMapper.toEntity(request);
        editor.setId(id);
        Editor updated = editorRepository.update(editor);
        return editorMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new RuntimeException("Editor not found with id: " + id);
        }
        editorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return editorRepository.existsById(id);
    }
}