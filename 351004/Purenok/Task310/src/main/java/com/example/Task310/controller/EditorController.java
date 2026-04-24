package com.example.Task310.controller;

import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import com.example.Task310.service.EditorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
@RequiredArgsConstructor
public class EditorController {

    private final EditorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo create(@Valid @RequestBody EditorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<EditorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(@PathVariable Long id, @Valid @RequestBody EditorRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
// НИКАКОГО КОДА НИЖЕ ЭТОЙ СКОБКИ БЫТЬ НЕ ДОЛЖНО!