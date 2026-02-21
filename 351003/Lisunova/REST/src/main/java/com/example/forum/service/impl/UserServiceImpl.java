package com.example.forum.service.impl;

import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.UserResponseTo;
import com.example.forum.entity.User;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.UserMapper;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserResponseTo create(UserRequestTo request) {
        validate(request);
        User user = mapper.toEntity(request);
        User saved = repository.save(user);
        return mapper.toResponse(saved);
    }

    @Override
    public UserResponseTo getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "40401"));
        return mapper.toResponse(user);
    }

    @Override
    public List<UserResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public UserResponseTo update(Long id, UserRequestTo request) {
        validate(request);

        User existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "40402"));

        mapper.updateEntityFromDto(request, existing);

        User updated = repository.update(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("User not found", "40403");
        }
        repository.deleteById(id);
    }


    private void validate(UserRequestTo request) {
        if (!StringUtils.hasText(request.getLogin()) ||
                request.getLogin().length() < 2 ||
                request.getLogin().length() > 64) {
            throw new BadRequestException("Invalid login", "40001");
        }
        if (!StringUtils.hasText(request.getPassword()) ||
                request.getPassword().length() < 8 ||
                request.getPassword().length() > 128) {
            throw new BadRequestException("Invalid password", "40002");
        }
    }
}
