package com.example.publisher.service;

import com.example.publisher.dto.request.EditorRequestTo;
import com.example.publisher.dto.response.EditorResponseTo;
import com.example.publisher.entity.Editor;
import com.example.publisher.exception.DuplicateException;
import com.example.publisher.exception.NotFoundException;
import com.example.publisher.mapper.EditorMapper;
import com.example.publisher.repository.EditorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class EditorService {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    public List<EditorResponseTo> getAllEditors() {
        return editorMapper.toResponseList(editorRepository.findAll());
    }

    public Page<EditorResponseTo> getAllEditors(Pageable pageable) {
        return editorRepository.findAll(pageable)
                .map(editorMapper::toResponse);
    }

    public EditorResponseTo getEditorById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));
        return editorMapper.toResponse(editor);
    }

    @Transactional
    public EditorResponseTo createEditor(@Valid EditorRequestTo request) {
        // Проверка уникальности логина
        if (editorRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateException("Editor with login '" + request.getLogin() + "' already exists", 40901);
        }

        Editor editor = editorMapper.toEntity(request);
        editor.setCreatedAt(LocalDateTime.now());
        editor.setModifiedAt(LocalDateTime.now());

        Editor savedEditor = editorRepository.save(editor);
        return editorMapper.toResponse(savedEditor);
    }

    @Transactional
    public EditorResponseTo updateEditor(Long id, @Valid EditorRequestTo request) {
        Editor existingEditor = editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found with id: " + id, 40401));

        // Проверка уникальности логина (исключая текущего редактора)
        if (editorRepository.existsByLoginAndIdNot(request.getLogin(), id)) {
            throw new DuplicateException("Editor with login '" + request.getLogin() + "' already exists", 40901);
        }

        editorMapper.updateEntity(request, existingEditor);
        existingEditor.setModifiedAt(LocalDateTime.now());

        Editor updatedEditor = editorRepository.save(existingEditor);
        return editorMapper.toResponse(updatedEditor);
    }

    @Transactional
    public void deleteEditor(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new NotFoundException("Editor not found with id: " + id, 40401);
        }
        editorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return editorRepository.existsById(id);
    }

    public Page<EditorResponseTo> searchEditors(String login, String firstName, String lastName, Pageable pageable) {
        return editorRepository.searchEditors(login, firstName, lastName, pageable)
                .map(editorMapper::toResponse);
    }
}