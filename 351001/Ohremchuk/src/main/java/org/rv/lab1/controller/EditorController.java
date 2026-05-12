package org.rv.lab1.controller;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.EditorRequestTo;
import org.rv.lab1.dto.EditorResponseTo;
import org.rv.lab1.service.EditorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/editors")
public class EditorController {
    private final EditorService service;

    public EditorController(EditorService service) {
        this.service = service;
    }

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
    public EditorResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(@PathVariable long id, @Valid @RequestBody EditorRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}

