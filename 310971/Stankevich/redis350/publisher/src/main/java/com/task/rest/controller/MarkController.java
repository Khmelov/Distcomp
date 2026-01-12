package com.task.rest.controller;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        log.info("GET /api/v1.0/marks/{}", id);
        return ResponseEntity.ok(markService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll() {
        log.info("GET /api/v1.0/marks");
        return ResponseEntity.ok(markService.getAllList());
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo request) {
        log.info("POST /api/v1.0/marks");
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkRequestTo request) {
        log.info("PUT /api/v1.0/marks/{}", id);
        return ResponseEntity.ok(markService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1.0/marks/{}", id);
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
