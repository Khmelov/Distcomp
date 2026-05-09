package com.example.Labs.service;
import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.EditorMapper;
import com.example.Labs.repository.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EditorResponseTo create(EditorRequestTo request) {
        // Если логин уже занят — бросаем исключение (тест ожидает 403)
        if (repository.findByLogin(request.getLogin()).isPresent()) {
            throw new DataIntegrityViolationException("Login already exists: " + request.getLogin());
        }
        Editor entity = mapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() == null || request.getRole().isBlank()) {
            entity.setRole("CUSTOMER");
        } else {
            entity.setRole(request.getRole().toUpperCase());
        }
        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EditorResponseTo> getAll(
            org.springframework.data.domain.Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public EditorResponseTo getById(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    @Transactional
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        // Сохраняем существующую роль если в запросе нет роли
        String existingRole = entity.getRole();

        mapper.updateEntity(request, entity);

        // Восстанавливаем роль если запрос не содержит роль
        if (request.getRole() == null || request.getRole().isBlank()) {
            entity.setRole(existingRole);
        } else {
            entity.setRole(request.getRole().toUpperCase());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Not found");
        repository.deleteById(id);
    }
}