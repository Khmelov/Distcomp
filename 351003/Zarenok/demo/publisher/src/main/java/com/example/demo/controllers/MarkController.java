package com.example.demo.controllers;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.service.MarkService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
@Validated
public class MarkController {
    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String name
            ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(markService.findAll(pageable, name));
        } else {
            return ResponseEntity.ok(markService.findAll(name));
        }
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable Long id) {
        MarkResponseTo mark = markService.findById(id);
        return ResponseEntity.ok(mark);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id,
                                                 @Valid @RequestBody MarkRequestTo dto) {
        MarkResponseTo updated = markService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
