package com.example.publisher.controller;

import com.example.publisher.dto.request.NoteRequestTo;
import com.example.publisher.dto.response.NoteResponseTo;
import com.example.publisher.service.NoteClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {

    // Внедряем сервис, который теперь управляет кэшем и Kafka
    private final NoteClientService service;

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