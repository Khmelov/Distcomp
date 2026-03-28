package com.example.Task310.controller;

import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import com.example.Task310.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts") // Проверьте, что тут именно posts
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<PostResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public PostResponseTo update(@PathVariable Long id, @Valid @RequestBody PostRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}