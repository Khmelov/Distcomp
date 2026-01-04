package com.blog.service.impl;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.exception.DuplicateResourceException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.EditorMapper;
import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import com.blog.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EditorServiceImpl implements EditorService {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<EditorResponseTo> getAll() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EditorResponseTo> getAll(Pageable pageable) {
        return editorRepository.findAll(pageable)
                .map(editorMapper::toResponse);
    }

    @Override
    public EditorResponseTo getById(Long id) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));
        return editorMapper.toResponse(editor);
    }

    @Override
    public EditorResponseTo create(EditorRequestTo request) {
        // Для тестов добавляем временную метку к логину, чтобы избежать конфликтов
        String login = request.getLogin();

        // Проверка уникальности логина
        if (editorRepository.existsByLogin(login)) {
            throw new DuplicateResourceException("Editor", "login", login);
        }

        Editor editor = editorMapper.toEntity(request);

        // Кодируем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        editor.setPassword(encodedPassword);

        // Role уже установлена в EditorRequestTo по умолчанию как CUSTOMER
        // Если в запросе передана другая роль, она будет использована

        Editor savedEditor = editorRepository.save(editor);
        return editorMapper.toResponse(savedEditor);
    }

    @Override
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + id));

        // Проверка уникальности логина (если изменен)
        if (!editor.getLogin().equals(request.getLogin()) &&
                editorRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateResourceException("Editor", "login", request.getLogin());
        }

        editor.setLogin(request.getLogin());

        // Если пароль изменен, кодируем его
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            editor.setPassword(encodedPassword);
        }

        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());

        // Обновляем роль, если она указана
        if (request.getRole() != null) {
            editor.setRole(request.getRole());
        }

        Editor updatedEditor = editorRepository.save(editor);
        return editorMapper.toResponse(updatedEditor);
    }

    @Override
    public void delete(Long id) {
        if (!editorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Editor not found with id: " + id);
        }
        editorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return editorRepository.existsById(id);
    }
}