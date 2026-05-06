package com.example.Task310.service;

import com.example.Task310.bean.Editor;
import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import com.example.Task310.exception.AlreadyExistsException;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.EditorMapper;
import com.example.Task310.repository.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository repository;
    private final EditorMapper mapper;

    @Transactional
    public EditorResponseTo create(EditorRequestTo request) {
        if (repository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("Editor with this login already exists");
        }
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<EditorResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public EditorResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found"));
    }

    @Transactional
    public EditorResponseTo update(EditorRequestTo request) {
        Editor existing = repository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found"));

        // Проверка уникальности логина при смене
        if (!existing.getLogin().equals(request.getLogin()) && repository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("Login already taken");
        }

        mapper.updateEntity(request, existing);
        return mapper.toResponse(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Editor not found");
        }
        repository.deleteById(id);
    }
}