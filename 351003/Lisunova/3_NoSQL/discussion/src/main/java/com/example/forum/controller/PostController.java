package com.example.forum.controller;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public PostResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<PostResponseTo> getAll(@RequestParam(required = false) Long topicId) {
        return service.getAll(topicId);
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