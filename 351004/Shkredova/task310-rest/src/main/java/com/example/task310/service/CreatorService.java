package com.example.task310.service;

import com.example.task310.dto.CreatorRequestTo;
import com.example.task310.dto.CreatorResponseTo;
import com.example.task310.exception.NotFoundException;
import com.example.task310.exception.ValidationException;
import com.example.task310.mapper.CreatorMapper;
import com.example.task310.model.Creator;
import com.example.task310.model.Role;
import com.example.task310.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreatorService {

    private final CreatorRepository creatorRepository;
    private final CreatorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @CachePut(value = "creators", key = "#result.id")
    @CacheEvict(cacheNames = "lists", key = "'all_creators'")
    public CreatorResponseTo create(CreatorRequestTo request) {
        return create(request, Role.CUSTOMER);
    }

    public CreatorResponseTo create(CreatorRequestTo request, Role role) {
        log.info("Создание нового Creator, логин: {}, роль: {}", request.getLogin(), role);
        validateCreate(request);

        if (creatorRepository.existsByLogin(request.getLogin())) {
            throw new DataIntegrityViolationException("Login already exists");
        }

        Creator entity = mapper.toEntity(request);
        entity.setRole(role);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));

        Creator saved = creatorRepository.save(entity);
        log.info("Creator создан с id = {}", saved.getId());
        return mapper.toResponse(saved);
    }

    public List<CreatorResponseTo> findAll() {
        log.info("Запрос всех Creator из БД");
        return creatorRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "creators", key = "#id")
    public CreatorResponseTo findById(Long id) {
        log.info("Кеш MISS: загрузка Creator id = {} из БД", id);
        Creator entity = creatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + id));
        return mapper.toResponse(entity);
    }

    @CachePut(value = "creators", key = "#id")
    @CacheEvict(cacheNames = "lists", key = "'all_creators'")
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        log.info("Обновление Creator id = {}", id);
        Creator existing = creatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + id));

        validateUpdate(request);

        existing.setLogin(request.getLogin());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        existing.setFirstname(request.getFirstname());
        existing.setLastname(request.getLastname());

        try {
            Creator updated = creatorRepository.save(existing);
            log.info("Creator id = {} обновлён, кеш обновлён", id);
            return mapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Duplicate login");
        }
    }

    @CacheEvict(cacheNames = {"creators", "lists"}, key = "#id")
    public void delete(Long id) {
        log.info("Удаление Creator id = {}", id);
        if (!creatorRepository.existsById(id)) {
            throw new NotFoundException("Creator not found with id: " + id);
        }
        creatorRepository.deleteById(id);
        log.info("Creator id = {} удалён, кеш очищен", id);
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