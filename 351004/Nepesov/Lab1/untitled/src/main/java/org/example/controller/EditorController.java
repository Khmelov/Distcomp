package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.service.EditorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
@RequiredArgsConstructor
public class EditorController {
    private final EditorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Возвращает 201
    public EditorResponseTo create(@Valid @RequestBody EditorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<EditorResponseTo> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping
    public EditorResponseTo update(@Valid @RequestBody EditorRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Возвращает 204
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}