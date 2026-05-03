package com.example.task310.service;

import com.example.task310.dto.WriterRequestTo;
import com.example.task310.dto.WriterResponseTo;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository repository;
    private final EntityMapper mapper;

    public List<WriterResponseTo> getAll(Pageable pageable) {
        return mapper.toWriterResponseList(repository.findAll(pageable).getContent());
    }

    public WriterResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Writer not found"));
    }

    public WriterResponseTo create(WriterRequestTo dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public WriterResponseTo update(WriterRequestTo dto) {
        if (!repository.existsById(dto.getId())) {
            throw new RuntimeException("Writer not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Writer not found");
        }
        repository.deleteById(id);
    }
}