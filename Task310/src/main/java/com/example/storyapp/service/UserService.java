package com.example.storyapp.service;

import com.example.storyapp.dto.UserRequestTo;
import com.example.storyapp.dto.UserResponseTo;
import com.example.storyapp.exception.AppException;
import com.example.storyapp.model.User;
import com.example.storyapp.repository.InMemoryUserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final InMemoryUserRepository repository;

    public UserService(InMemoryUserRepository repository) {
        this.repository = repository;
    }

    public List<UserResponseTo> getAllUsers() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponseTo getUserById(@NotNull Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("User not found", 40401));
    }

    public UserResponseTo createUser(@Valid UserRequestTo request) {
        if (repository.findByLogin(request.login()) != null) {
            throw new AppException("Login already exists", 40902);
        }
        User user = toEntity(request);
        User saved = repository.save(user);
        return toResponse(saved);
    }

    public UserResponseTo updateUser(@Valid UserRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!repository.findById(request.id()).isPresent()) {
            throw new AppException("User not found for update", 40404);
        }
        User user = toEntity(request);
        User updated = repository.save(user);
        return toResponse(updated);
    }

    public void deleteUser(@NotNull Long id) {
        if (!repository.deleteById(id)) {
            throw new AppException("User not found for deletion", 40405);
        }
    }

    private User toEntity(UserRequestTo dto) {
        User user = new User();
        user.setId(dto.id());
        user.setLogin(dto.login());
        user.setPassword(dto.password());
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        return user;
    }

    private UserResponseTo toResponse(User user) {
        return new UserResponseTo(
                user.getId(),
                user.getLogin(),
                user.getFirstname(),
                user.getLastname()
        );
    }
}