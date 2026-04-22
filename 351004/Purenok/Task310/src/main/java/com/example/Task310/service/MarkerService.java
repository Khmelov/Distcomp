package com.example.Task310.service;

import com.example.Task310.bean.Marker;
import com.example.Task310.dto.MarkerRequestTo;
import com.example.Task310.dto.MarkerResponseTo;
import com.example.Task310.exception.AlreadyExistsException;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.MarkerMapper;
import com.example.Task310.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository repository;
    private final MarkerMapper mapper;

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo request) {
        if (repository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Marker name already exists");
        }
        Marker entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<MarkerResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public MarkerResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found"));
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found"));

        // Проверка уникальности имени при переименовании
        if (!existing.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Marker name already exists");
        }

        mapper.updateEntity(request, existing);
        return mapper.toResponse(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Marker not found");
        }
        repository.deleteById(id);
    }
}