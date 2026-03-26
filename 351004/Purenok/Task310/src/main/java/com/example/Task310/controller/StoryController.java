package com.example.Task310.controller;

import com.example.Task310.dto.StoryRequestTo;
import com.example.Task310.dto.StoryResponseTo;
import com.example.Task310.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@Valid @RequestBody StoryRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<StoryResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public StoryResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public StoryResponseTo update(@PathVariable Long id, @Valid @RequestBody StoryRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}