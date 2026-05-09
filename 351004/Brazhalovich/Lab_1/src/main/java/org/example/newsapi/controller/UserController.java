package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.UserRequestTo;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo create(@RequestBody @Valid UserRequestTo request) {
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponseTo> getAll(@PageableDefault(size = 50) Pageable pageable) {
        // Возвращаем только контент списка, чтобы тестер не путался в мета-данных Page
        return userService.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public UserResponseTo getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponseTo update(@PathVariable Long id, @RequestBody @Valid UserRequestTo request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}