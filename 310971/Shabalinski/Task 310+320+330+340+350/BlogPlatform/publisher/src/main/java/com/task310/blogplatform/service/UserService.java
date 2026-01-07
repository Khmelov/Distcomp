package com.task310.blogplatform.service;

import com.task310.blogplatform.dto.UserRequestTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.exception.DuplicateException;
import com.task310.blogplatform.exception.EntityNotFoundException;
import com.task310.blogplatform.exception.ValidationException;
import com.task310.blogplatform.mapper.UserMapper;
import com.task310.blogplatform.model.User;
import com.task310.blogplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Caching(evict = {
        @CacheEvict(value = "users", allEntries = true)
    })
    public UserResponseTo create(UserRequestTo dto) {
        validateUserRequest(dto);
        
        // Check for duplicate login
        if (repository.findByLogin(dto.getLogin().trim()).isPresent()) {
            throw new DuplicateException("User with login '" + dto.getLogin() + "' already exists");
        }
        
        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        return mapper.toResponseDto(saved);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<UserResponseTo> findAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseTo findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user id");
        }
        return repository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "users", key = "'all'")
    })
    public UserResponseTo update(Long id, UserRequestTo dto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user id");
        }
        validateUserRequest(dto);
        User existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        // Check for duplicate login (excluding current user)
        repository.findByLogin(dto.getLogin().trim())
                .ifPresent(user -> {
                    if (!user.getId().equals(id)) {
                        throw new DuplicateException("User with login '" + dto.getLogin() + "' already exists");
                    }
                });
        
        mapper.updateEntityFromDto(dto, existing);
        User updated = repository.save(existing);
        return mapper.toResponseDto(updated);
    }

    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "users", key = "'all'")
    })
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user id");
        }
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void validateUserRequest(UserRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("User data is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id must not be provided in request body");
        }
        if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (dto.getLogin().trim().length() < 2) {
            throw new ValidationException("Login must be at least 2 characters long");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (dto.getPassword().trim().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        if (dto.getFirstname() == null || dto.getFirstname().trim().isEmpty()) {
            throw new ValidationException("Firstname is required");
        }
        if (dto.getFirstname().trim().length() < 2) {
            throw new ValidationException("Firstname must be at least 2 characters long");
        }
        if (dto.getLastname() == null || dto.getLastname().trim().isEmpty()) {
            throw new ValidationException("Lastname is required");
        }
        if (dto.getLastname().trim().length() < 2) {
            throw new ValidationException("Lastname must be at least 2 characters long");
        }
    }
}

