package com.example.publisher.controller;

import com.example.publisher.dto.UserRequestTo;
import com.example.publisher.dto.UserResponseTo;
import com.example.publisher.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo create(@Valid @RequestBody UserRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<UserResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public UserResponseTo get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public UserResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody UserRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}