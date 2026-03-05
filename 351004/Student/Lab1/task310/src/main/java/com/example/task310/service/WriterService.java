package com.example.task310.service;

import com.example.task310.dto.WriterRequestTo;
import com.example.task310.dto.WriterResponseTo;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository repository;
    private final EntityMapper mapper;

    public WriterResponseTo create(WriterRequestTo dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public WriterResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Writer not found"));
    }

    public List<WriterResponseTo> getAll() {
        return mapper.toWriterResponseList(repository.findAll());
    }

    public WriterResponseTo update(WriterRequestTo dto) {
        if (repository.findById(dto.getId()).isEmpty()) {
            throw new RuntimeException("Writer not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new RuntimeException("Writer not found");
        }
        repository.deleteById(id);
    }
}