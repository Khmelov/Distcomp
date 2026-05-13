package com.example.Task310.controller;

import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import com.example.Task310.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<PostResponseTo> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PostResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ИСПРАВЛЕННЫЙ МЕТОД
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