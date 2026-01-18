package com.rest.restapp.service;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import com.rest.restapp.entity.User;
import com.rest.restapp.exception.DuplicateException;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.exception.ValidationException;
import com.rest.restapp.mapper.UserMapper;
import com.rest.restapp.repositry.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository repository;
    UserMapper mapper;

    @Transactional
    public UserResponseToDto createUser(UserRequestToDto requestTo) {
        if (repository.existsByLogin(requestTo.login())) {
            throw new DuplicateException("This user already exists");
        }
        validateUserRequest(requestTo);
        var user = mapper.toEntity(requestTo);
        var savedUser = repository.save(user);
        return mapper.toResponseTo(savedUser);
    }

    public UserResponseToDto getUserById(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return mapper.toResponseTo(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseToDto> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public UserResponseToDto updateUser(Long id, UserRequestToDto requestTo) {
        validateUserRequest(requestTo);
        var existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        mapper.updateEntityFromDto(requestTo, existingUser);
        User updatedUser = repository.save(existingUser);
        return mapper.toResponseTo(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    private void validateUserRequest(UserRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("User request cannot be null");
        }
        if (requestTo.login() == null || requestTo.login().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (requestTo.password() == null || requestTo.password().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (requestTo.firstname() == null || requestTo.firstname().trim().isEmpty()) {
            throw new ValidationException("Firstname is required");
        }
        if (requestTo.lastname() == null || requestTo.lastname().trim().isEmpty()) {
            throw new ValidationException("Lastname is required");
        }
    }
}