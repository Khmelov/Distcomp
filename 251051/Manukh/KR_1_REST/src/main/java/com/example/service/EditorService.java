package com.example.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.dto.request.EditorRequestTo;
import com.example.dto.response.EditorResponseTo;
import com.example.exception.DuplicateException;
import com.example.exception.NotFoundException;
import com.example.mapper.EditorMapper;
import com.example.model.Editor;
import com.example.repository.InMemoryEditorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class EditorService {

    @Autowired
    private InMemoryEditorRepository editorRepository;

    public List<EditorResponseTo> getAllEditors() {
        return editorRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EditorResponseTo getEditorById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));
        return convertToResponse(editor);
    }

    public EditorResponseTo createEditor(@Valid EditorRequestTo request) {
        editorRepository.findByLogin(request.getLogin())
                .ifPresent(editor -> {
                    throw new DuplicateException("Editor with login '" + request.getLogin() + "' already exists", 40901);
                });

        Editor editor = convertToEntity(request);
        Editor savedEditor = editorRepository.save(editor);
        return convertToResponse(savedEditor);
    }

    public EditorResponseTo updateEditor(Long id, @Valid EditorRequestTo request) {
        Editor existingEditor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));

        editorRepository.findByLogin(request.getLogin())
                .ifPresent(editor -> {
                    if (!editor.getId().equals(id)) {
                        throw new DuplicateException("Editor with login '" + request.getLogin() + "' already exists", 40901);
                    }
                });

        existingEditor.setLogin(request.getLogin());
        existingEditor.setPassword(request.getPassword());
        existingEditor.setFirstname(request.getFirstname());
        existingEditor.setLastname(request.getLastname());

        Editor updatedEditor = editorRepository.update(existingEditor);
        return convertToResponse(updatedEditor);
    }

    public void deleteEditor(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new NotFoundException("Editor not found with id: " + id, 40401);
        }
        editorRepository.deleteById(id);
    }

    private Editor convertToEntity(EditorRequestTo request) {
        Editor editor = new Editor();
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());
        editor.setCreated(LocalDateTime.now());
        editor.setModified(LocalDateTime.now());
        return editor;
    }

    private EditorResponseTo convertToResponse(Editor editor) {
        EditorResponseTo response = new EditorResponseTo();
        response.setId(editor.getId());
        response.setLogin(editor.getLogin());
        response.setFirstname(editor.getFirstname());
        response.setLastname(editor.getLastname());
        response.setCreated(editor.getCreated());
        response.setModified(editor.getModified());
        return response;
    }
}