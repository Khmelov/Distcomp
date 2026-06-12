package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Удалил импорт UUID, он тут больше не нужен

@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@Valid @RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<PostResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return service.findAll(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public PostResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ВОТ ЭТОГО МЕТОДА НЕ ХВАТАЛО
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo update(@Valid @RequestBody PostRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}