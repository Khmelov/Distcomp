package com.example.Labs.service;

import com.example.Labs.dto.request.MarkRequestTo;
import com.example.Labs.dto.response.MarkResponseTo;
import com.example.Labs.entity.Mark;
import com.example.Labs.exception.ResourceNotFoundException;
import com.example.Labs.mapper.MarkMapper;
import com.example.Labs.repository.InMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final InMemoryRepository<Mark> markRepository;
    private final MarkMapper mapper;

    public MarkResponseTo create(MarkRequestTo request) {
        Mark entity = mapper.toEntity(request);
        return mapper.toDto(markRepository.save(entity));
    }

    public List<MarkResponseTo> getAll() {
        return markRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkResponseTo getById(Long id) {
        Mark entity = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark not found with id: " + id));
        return mapper.toDto(entity);
    }

    public MarkResponseTo update(Long id, MarkRequestTo request) {
        Mark entity = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark not found with id: " + id));
        mapper.updateEntity(request, entity);
        return mapper.toDto(markRepository.update(entity));
    }

    public void delete(Long id) {
        if (!markRepository.deleteById(id)) {
            throw new ResourceNotFoundException("Mark not found with id: " + id);
        }
    }
}
