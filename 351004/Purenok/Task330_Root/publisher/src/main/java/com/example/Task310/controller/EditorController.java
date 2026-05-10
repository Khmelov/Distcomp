package com.example.Task310.controller;

import com.example.Task310.dto.EditorRequestTo;
import com.example.Task310.dto.EditorResponseTo;
import com.example.Task310.service.EditorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editors")
@RequiredArgsConstructor
public class EditorController {
    private final EditorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo create(@Valid @RequestBody EditorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<EditorResponseTo> findAll(Pageable pageable) {
        return service.findAll(pageable);
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}