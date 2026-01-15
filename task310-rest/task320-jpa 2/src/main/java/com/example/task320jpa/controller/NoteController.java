package com.example.task320jpa.controller;

import com.example.task320jpa.dto.request.NoteRequestTo;
import com.example.task320jpa.dto.response.NoteResponseTo;
import com.example.task320jpa.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    
    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@Valid @RequestBody NoteRequestTo requestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.create(requestTo));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<Page<NoteResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort[1]), sort[0]));
        return ResponseEntity.ok(noteService.getAll(pageable));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseTo> update(@PathVariable Long id, @Valid @RequestBody NoteRequestTo requestTo) {
        return ResponseEntity.ok(noteService.update(id, requestTo));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
