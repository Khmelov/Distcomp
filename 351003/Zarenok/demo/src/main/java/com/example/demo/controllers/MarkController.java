package com.example.demo.controllers;

import com.example.demo.dto.requests.MarkRequestTo;
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
    public ResponseEntity<Page<MarkResponseTo>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        return ResponseEntity.ok(markService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        MarkResponseTo mark = markService.findById(id);
        return ResponseEntity.ok(mark);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@Valid @PathVariable Long id,
                                                  @RequestBody MarkRequestTo dto) throws ChangeSetPersister.NotFoundException {
        MarkResponseTo updated = markService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
