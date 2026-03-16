package com.example.Task310.service;

import com.example.Task310.bean.Marker;
import com.example.Task310.dto.MarkerRequestTo;
import com.example.Task310.dto.MarkerResponseTo;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.MarkerMapper;
import com.example.Task310.repository.InMemoryMarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerService {

    private final InMemoryMarkerRepository repository;
    private final MarkerMapper mapper;

    public MarkerResponseTo create(MarkerRequestTo request) {
        Marker marker = mapper.toEntity(request);
        return mapper.toDto(repository.save(marker));
    }

    public List<MarkerResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo getById(Long id) {
        Marker marker = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found with id: " + id));
        return mapper.toDto(marker);
    }

    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker marker = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found with id: " + id));

        mapper.updateEntityFromDto(request, marker);
        return mapper.toDto(repository.update(marker));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Marker not found with id: " + id);
        }
        repository.deleteById(id);
    }
}