package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository repository;
    private final EntityMapper mapper;

    public List<MarkerResponseTo> getAll(Pageable pageable) {
        return mapper.toMarkerResponseList(repository.findAll(pageable).getContent());
    }

    public MarkerResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new RuntimeException("Marker not found"));
    }

    public MarkerResponseTo create(MarkerRequestTo dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public MarkerResponseTo update(MarkerRequestTo dto) {
        if (!repository.existsById(dto.getId())) throw new RuntimeException("Marker not found");
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Marker not found");
        repository.deleteById(id);
    }
}