package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @GetMapping
    public List<PostResponseTo> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PostResponseTo findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseTo create(@RequestBody PostRequestTo request) {
        return service.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PostResponseTo update(@RequestBody PostRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}