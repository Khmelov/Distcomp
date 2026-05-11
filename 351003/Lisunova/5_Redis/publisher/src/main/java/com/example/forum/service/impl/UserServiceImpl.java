package com.example.forum.service.impl;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;
import com.example.forum.entity.User;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.UserMapper;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id", unless = "#result == null")
    public UserResponseTo getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "40401"));
        return mapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseTo> getAll(String loginFilter, Pageable pageable) {
        Page<User> page = (StringUtils.hasText(loginFilter))
                ? repository.findByLoginContainingIgnoreCase(loginFilter, pageable)
                : repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponseTo create(UserRequestTo dto) {
        validate(dto);
        repository.findByLogin(dto.getLogin()).ifPresent(u -> {
            throw new BadRequestException("Login already exists", "40301");
        });

        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public UserResponseTo update(Long id, UserRequestTo dto) {
        validate(dto);

        User existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "40402"));

        repository.findByLogin(dto.getLogin())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new BadRequestException("Login already taken", "40301");
                });

        mapper.updateEntityFromDto(dto, existing);
        User updated = repository.save(existing);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("User not found", "40403");
        }
        repository.deleteById(id);
    }

    private void validate(UserRequestTo dto) {
        if (dto.getLogin() == null || dto.getLogin().length() < 2 || dto.getLogin().length() > 64) {
            throw new BadRequestException("Invalid login", "40001");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8 || dto.getPassword().length() > 128) {
            throw new BadRequestException("Invalid password", "40002");
        }
        if (dto.getFirstname() == null || dto.getFirstname().length() < 2 || dto.getFirstname().length() > 64) {
            throw new BadRequestException("Invalid firstname", "40003");
        }
        if (dto.getLastname() == null || dto.getLastname().length() < 2 || dto.getLastname().length() > 64) {
            throw new BadRequestException("Invalid lastname", "40004");
        }
    }
}