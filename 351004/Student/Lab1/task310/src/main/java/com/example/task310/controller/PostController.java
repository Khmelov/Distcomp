package com.example.task310.controller;

import com.example.task310.dto.PostRequestTo;
import com.example.task310.dto.PostResponseTo;
import com.example.task310.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @GetMapping
    public List<PostResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping
    public PostResponseTo update(@Valid @RequestBody PostRequestTo dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}