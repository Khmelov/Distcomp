package com.example.task310.service;

import com.example.task310.dto.WriterRequestTo;
import com.example.task310.dto.WriterResponseTo;
import com.example.task310.entity.Writer;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository repository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public List<WriterResponseTo> getAll(Pageable pageable) {
        return mapper.toWriterResponseList(repository.findAll(pageable).getContent());
    }

    @Cacheable(value = "writers", key = "#id")
    public WriterResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Writer not found"));
    }

    public WriterResponseTo create(WriterRequestTo dto) {
        Writer entity = mapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = "writers", key = "#dto.id")
    public WriterResponseTo update(WriterRequestTo dto) {
        if (!repository.existsById(dto.getId())) {
            throw new RuntimeException("Writer not found");
        }
        Writer existing = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Writer not found"));

        Writer entity = mapper.toEntity(dto);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            entity.setPassword(existing.getPassword());
        }
        entity.setRole(existing.getRole());
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = "writers", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Writer not found");
        }
        repository.deleteById(id);
    }

    public Writer getEntityByLogin(String login) {
        return repository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Writer not found"));
    }

    public boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }
}
