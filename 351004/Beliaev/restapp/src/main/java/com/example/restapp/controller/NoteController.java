package com.example.restapp.controller;

import com.example.restapp.dto.request.NoteRequestTo;
import com.example.restapp.dto.response.NoteResponseTo;
import com.example.restapp.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo create(@Valid @RequestBody NoteRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<NoteResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public NoteResponseTo update(@PathVariable Long id, @Valid @RequestBody NoteRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}