package com.example.task320jpa.controller;

import com.example.task320jpa.dto.request.MarkRequestTo;
import com.example.task320jpa.dto.response.MarkResponseTo;
import com.example.task320jpa.service.MarkService;
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
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;
    
    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo requestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(requestTo));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<Page<MarkResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort[1]), sort[0]));
        return ResponseEntity.ok(markService.getAll(pageable));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id, @Valid @RequestBody MarkRequestTo requestTo) {
        return ResponseEntity.ok(markService.update(id, requestTo));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
