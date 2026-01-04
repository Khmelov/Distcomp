package com.example.publisher.service;

import com.example.publisher.dto.UserRequestTo;
import com.example.publisher.dto.UserResponseTo;
import com.example.publisher.exception.AppException;
import com.example.publisher.model.User;
import com.example.publisher.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UserResponseTo> getAllUsers() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseTo getUserById(@NotNull Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("User not found", 40401));
    }

    public UserResponseTo createUser(@Valid UserRequestTo request) {
        if (repository.existsByLogin(request.login())) {
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

        User existing = repository.findById(request.id())
                .orElseThrow(() -> new AppException("User not found for update", 40404));

        // Проверка дубликата login (кроме текущего)
        if (!existing.getLogin().equals(request.login()) &&
                repository.existsByLogin(request.login())) {
            throw new AppException("Login already exists", 40902);
        }

        updateFromDto(existing, request);
        User updated = repository.save(existing);
        return toResponse(updated);
    }

    public void deleteUser(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new AppException("User not found for deletion", 40405);
        }
        repository.deleteById(id);
    }

    // === Маппинг ===
    private User toEntity(UserRequestTo dto) {
        return new User(dto.login(), dto.password(), dto.firstname(), dto.lastname());
    }

    private void updateFromDto(User user, UserRequestTo dto) {
        user.setLogin(dto.login());
        user.setPassword(dto.password());
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
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