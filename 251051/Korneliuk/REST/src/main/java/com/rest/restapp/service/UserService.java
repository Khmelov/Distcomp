package com.rest.restapp.service;

import com.rest.restapp.dto.request.UserRequestToDto;
import com.rest.restapp.dto.response.UserResponseToDto;
import com.rest.restapp.entity.User;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.UserMapper;
import com.rest.restapp.repository.InMemoryRepository;
import jakarta.validation.ValidationException;
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

    InMemoryRepository repository;
    UserMapper mapper;

    @Transactional
    public UserResponseToDto createUser(UserRequestToDto requestTo) {
        validateAuthorRequest(requestTo);
        var author = mapper.toEntity(requestTo);
        var savedAuthor = repository.saveUser(author);
        return mapper.toResponseTo(savedAuthor);
    }

    public UserResponseToDto getUserById(Long id) {
        var author = repository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
        return mapper.toResponseTo(author);
    }

    @Transactional(readOnly = true)
    public List<UserResponseToDto> getAllUsers() {
        return repository.findAllUsers().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public UserResponseToDto updateUserr(Long id, UserRequestToDto requestTo) {
        validateAuthorRequest(requestTo);
        var existingAuthor = repository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));

        mapper.updateEntityFromDto(requestTo, existingAuthor);
        User updatedUser = repository.saveUser(existingAuthor);
        return mapper.toResponseTo(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsUserById(id)) {
            throw new NotFoundException("Author with id " + id + " not found");
        }
        repository.deleteUserById(id);
    }

    private void validateAuthorRequest(UserRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Author request cannot be null");
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