package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.WriterRequestTo;
import com.example.entitiesapp.dto.response.WriterResponseTo;
import com.example.entitiesapp.entities.Writer;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.WriterMapper;
import com.example.entitiesapp.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    public List<WriterResponseTo> getAll() {
        return writerRepository.findAll().stream()
                .map(writerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public WriterResponseTo getById(Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + id));
        return writerMapper.toResponseDto(writer);
    }

    public WriterResponseTo create(WriterRequestTo dto) {
        validateWriterRequest(dto);

        if (writerRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new ValidationException("Writer with login '" + dto.getLogin() + "' already exists");
        }

        Writer writer = writerMapper.toEntity(dto);
        writer.setCreated(LocalDateTime.now());
        writer.setModified(LocalDateTime.now());

        Writer saved = writerRepository.save(writer);
        return writerMapper.toResponseDto(saved);
    }

    public WriterResponseTo update(Long id, WriterRequestTo dto) {
        validateWriterRequest(dto);

        Writer existing = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + id));

        Writer writer = writerMapper.toEntity(dto);
        writer.setId(id);
        writer.setCreated(existing.getCreated());
        writer.setModified(LocalDateTime.now());
        writer.setArticles(existing.getArticles());

        Writer updated = writerRepository.update(writer);
        return writerMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Writer not found with id: " + id);
        }
        writerRepository.deleteById(id);
    }

    private void validateWriterRequest(WriterRequestTo dto) {
        if (dto.getLogin() == null || dto.getLogin().length() < 2 || dto.getLogin().length() > 64) {
            throw new ValidationException("Login must be between 2 and 64 characters");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8 || dto.getPassword().length() > 128) {
            throw new ValidationException("Password must be between 8 and 128 characters");
        }
        if (dto.getFirstname() == null || dto.getFirstname().length() < 2 || dto.getFirstname().length() > 64) {
            throw new ValidationException("Firstname must be between 2 and 64 characters");
        }
        if (dto.getLastname() == null || dto.getLastname().length() < 2 || dto.getLastname().length() > 64) {
            throw new ValidationException("Lastname must be between 2 and 64 characters");
        }
    }
}