package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.EditorRequestTo;
import org.example.dto.EditorResponseTo;
import org.example.mapper.EditorMapper;
import org.example.model.Editor;
import org.example.service.EditorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/editors")
@RequiredArgsConstructor
public class EditorController {

    private final EditorService service;
    private final EditorMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo create(@Valid @RequestBody EditorRequestTo request) {
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(service.create(entity));
    }

    @GetMapping
    public List<EditorResponseTo> findAll() {
        return service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EditorResponseTo findById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @PutMapping
    public EditorResponseTo update(@Valid @RequestBody EditorRequestTo request) {
        Editor entity = mapper.toEntity(request);
        return mapper.toResponse(service.update(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}