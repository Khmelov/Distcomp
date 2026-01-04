package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.WriterRequestTo;
import com.example.entitiesapp.dto.response.WriterResponseTo;
import com.example.entitiesapp.entities.Writer;
import com.example.entitiesapp.exceptions.DuplicateResourceException;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.WriterMapper;
import com.example.entitiesapp.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    @Transactional
    public WriterResponseTo create(WriterRequestTo dto) {
        validateWriterRequest(dto);

        if (writerRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateResourceException("Writer with login '" + dto.getLogin() + "' already exists");
        }

        Writer writer = writerMapper.toEntity(dto);
        Writer saved = writerRepository.save(writer);
        return writerMapper.toResponseDto(saved);
    }

    @Transactional
    public WriterResponseTo update(Long id, WriterRequestTo dto) {
        validateWriterRequest(dto);

        Writer existing = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + id));

        if (!existing.getLogin().equals(dto.getLogin()) &&
                writerRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateResourceException("Writer with login '" + dto.getLogin() + "' already exists");
        }

        existing.setLogin(dto.getLogin());
        existing.setPassword(dto.getPassword());
        existing.setFirstName(dto.getFirstname());
        existing.setLastName(dto.getLastname());

        Writer updated = writerRepository.save(existing);
        return writerMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Writer not found with id: " + id);
        }

        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + id));

        if (!writer.getArticles().isEmpty()) {
            throw new ValidationException("Cannot delete writer with id " + id +
                    " because they have " + writer.getArticles().size() + " articles");
        }

        writerRepository.deleteById(id);
    }

    private void validateWriterRequest(WriterRequestTo dto) {
        if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (dto.getLogin().length() < 2 || dto.getLogin().length() > 64) {
            throw new ValidationException("Login must be between 2 and 64 characters");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (dto.getPassword().length() < 8 || dto.getPassword().length() > 128) {
            throw new ValidationException("Password must be between 8 and 128 characters");
        }
        if (dto.getFirstname() == null || dto.getFirstname().trim().isEmpty()) {
            throw new ValidationException("Firstname is required");
        }
        if (dto.getFirstname().length() < 2 || dto.getFirstname().length() > 64) {
            throw new ValidationException("Firstname must be between 2 and 64 characters");
        }
        if (dto.getLastname() == null || dto.getLastname().trim().isEmpty()) {
            throw new ValidationException("Lastname is required");
        }
        if (dto.getLastname().length() < 2 || dto.getLastname().length() > 64) {
            throw new ValidationException("Lastname must be between 2 and 64 characters");
        }
    }

    public boolean existsById(Long id) {
        return writerRepository.existsById(id);
    }

    public WriterResponseTo findByLogin(String login) {
        Writer writer = writerRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with login: " + login));
        return writerMapper.toResponseDto(writer);
    }
}