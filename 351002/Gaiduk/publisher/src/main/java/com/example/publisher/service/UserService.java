package com.example.publisher.service;

import com.example.publisher.dto.UserRequestTo;
import com.example.publisher.dto.UserResponseTo;
import com.example.publisher.entity.User;
import com.example.publisher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    @Transactional
    public UserResponseTo create(UserRequestTo dto) {
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        return toResponse(repo.save(user));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseTo> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponseTo get(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserResponseTo update(Long id, UserRequestTo dto) {
        User user = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        return toResponse(repo.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        repo.deleteById(id);
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