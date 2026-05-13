package com.example.discussion.controller;

import com.example.discussion.dto.ReactionRequestTo;
import com.example.discussion.dto.ReactionResponseTo;
import com.example.discussion.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionService service;

    public ReactionController(ReactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@Valid @RequestBody ReactionRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ReactionResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ReactionResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody ReactionRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}