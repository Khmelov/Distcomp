package com.example.task310.service;

import com.example.task310.dto.MarkerRequestTo;
import com.example.task310.dto.MarkerResponseTo;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository repository;
    private final EntityMapper mapper;

    public MarkerResponseTo create(MarkerRequestTo dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public MarkerResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Marker not found"));
    }

    public List<MarkerResponseTo> getAll() {
        return mapper.toMarkerResponseList(repository.findAll());
    }

    public MarkerResponseTo update(MarkerRequestTo dto) {
        if (repository.findById(dto.getId()).isEmpty()) {
            throw new RuntimeException("Marker not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new RuntimeException("Marker not found");
        }
        repository.deleteById(id);
    }
}