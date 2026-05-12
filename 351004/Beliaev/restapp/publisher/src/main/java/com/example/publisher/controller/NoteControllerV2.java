package com.example.publisher.controller;

import com.example.publisher.dto.request.NoteRequestTo;
import com.example.publisher.dto.response.NoteResponseTo;
import com.example.publisher.service.NoteClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/notes")
@RequiredArgsConstructor
public class NoteControllerV2 {
    private final NoteClientService service;

    // Комментарии могут оставлять все авторизованные
    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@Valid @RequestBody NoteRequestTo request) {
        NoteResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<NoteResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // Редактировать и удалять комментарии (согласно простой логике) может только ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NoteResponseTo update(@PathVariable Long id, @Valid @RequestBody NoteRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}