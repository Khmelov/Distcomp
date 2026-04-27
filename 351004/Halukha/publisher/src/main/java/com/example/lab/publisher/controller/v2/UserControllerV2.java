package com.example.lab.publisher.controller.v2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab.publisher.dto.UserRequestTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.exception.EntityNotFoundException;
import com.example.lab.publisher.mapper.UserMapper;
import com.example.lab.publisher.model.User;
import com.example.lab.publisher.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2.0/users")
public class UserControllerV2 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper = UserMapper.INSTANCE;

    public UserControllerV2(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseTo>> getAllUsers() {
        List<UserResponseTo> users = userRepository.findAll().stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseTo> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.canAccessUser(#id)")
    public ResponseEntity<UserResponseTo> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestTo req) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));

        User updated = mapper.updateEntity(req, existing);
        updated.setId(id);
        updated.setPassword(passwordEncoder.encode(req.getPassword()));
        User saved = userRepository.save(updated);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.canAccessUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found", 40401);
        }
        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    static Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

