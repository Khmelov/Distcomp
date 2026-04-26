package com.example.task310.service;

import com.example.task310.dto.CreatorRequestTo;
import com.example.task310.dto.CreatorResponseTo;
import com.example.task310.exception.NotFoundException;
import com.example.task310.exception.ValidationException;
import com.example.task310.mapper.CreatorMapper;
import com.example.task310.model.Creator;
import com.example.task310.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatorService {
    private final CreatorRepository creatorRepository;
    private final CreatorMapper mapper;

    public CreatorResponseTo create(CreatorRequestTo request) {
        validateCreate(request);

        try {
            Creator entity = mapper.toEntity(request);
            Creator saved = creatorRepository.save(entity);
            return mapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    public List<CreatorResponseTo> findAll() {
        return creatorRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public CreatorResponseTo findById(Long id) {
        return creatorRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + id));
    }

    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        Creator existing = creatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + id));

        validateUpdate(request);

        existing.setLogin(request.getLogin());
        existing.setPassword(request.getPassword());
        existing.setFirstname(request.getFirstname());
        existing.setLastname(request.getLastname());

        try {
            Creator updated = creatorRepository.save(existing);
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("duplicate key value violates unique constraint");
        }
    }

    public void delete(Long id) {
        if (!creatorRepository.existsById(id)) {
            throw new NotFoundException("Creator not found with id: " + id);
        }
        creatorRepository.deleteById(id);
    }

    private void validateCreate(CreatorRequestTo request) {
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (request.getLogin().length() < 2 || request.getLogin().length() > 64) {
            throw new ValidationException("Login must be between 2 and 64 characters");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (request.getPassword().length() < 8 || request.getPassword().length() > 128) {
            throw new ValidationException("Password must be between 8 and 128 characters");
        }
        if (request.getFirstname() == null || request.getFirstname().trim().isEmpty()) {
            throw new ValidationException("Firstname is required");
        }
        if (request.getFirstname().length() < 2 || request.getFirstname().length() > 64) {
            throw new ValidationException("Firstname must be between 2 and 64 characters");
        }
        if (request.getLastname() == null || request.getLastname().trim().isEmpty()) {
            throw new ValidationException("Lastname is required");
        }
        if (request.getLastname().length() < 2 || request.getLastname().length() > 64) {
            throw new ValidationException("Lastname must be between 2 and 64 characters");
        }
    }

    private void validateUpdate(CreatorRequestTo request) {
        if (request.getLogin() != null && (request.getLogin().length() < 2 || request.getLogin().length() > 64)) {
            throw new ValidationException("Login must be between 2 and 64 characters");
        }
        if (request.getPassword() != null && (request.getPassword().length() < 8 || request.getPassword().length() > 128)) {
            throw new ValidationException("Password must be between 8 and 128 characters");
        }
        if (request.getFirstname() != null && (request.getFirstname().length() < 2 || request.getFirstname().length() > 64)) {
            throw new ValidationException("Firstname must be between 2 and 64 characters");
        }
        if (request.getLastname() != null && (request.getLastname().length() < 2 || request.getLastname().length() > 64)) {
            throw new ValidationException("Lastname must be between 2 and 64 characters");
        }
    }
}