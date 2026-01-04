package com.task.rest.controller;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        log.info("GET request for mark with id: {}", id);
        return ResponseEntity.ok(markService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET request for all marks");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(markService.getAll(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo requestTo) {
        log.info("POST request to create mark: {}", requestTo.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(requestTo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkRequestTo requestTo) {
        log.info("PUT request to update mark with id: {}", id);
        return ResponseEntity.ok(markService.update(id, requestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE request for mark with id: {}", id);
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
