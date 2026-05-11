package com.example.Task310.controller;

import com.example.Task310.dto.StoryRequestTo;
import com.example.Task310.dto.StoryResponseTo;
import com.example.Task310.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable; // Добавьте импорт
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@Valid @RequestBody StoryRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<StoryResponseTo> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public StoryResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // ИСПРАВЛЕННЫЙ МЕТОД: добавили {id} в путь и передаем два аргумента в сервис
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