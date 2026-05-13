package org.rv.lab1.controller;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.CommentRequestTo;
import org.rv.lab1.dto.CommentResponseTo;
import org.rv.lab1.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/comments")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@Valid @RequestBody CommentRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<CommentResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CommentResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable long id, @Valid @RequestBody CommentRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}

