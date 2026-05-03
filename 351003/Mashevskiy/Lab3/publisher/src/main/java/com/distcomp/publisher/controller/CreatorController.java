package com.distcomp.publisher.controller;

import com.distcomp.publisher.dto.CreatorRequestDTO;
import com.distcomp.publisher.dto.CreatorResponseDTO;
import com.distcomp.publisher.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    @Autowired
    private CreatorService creatorService;

    @GetMapping
    public ResponseEntity<List<CreatorResponseDTO>> getAll() {
        return ResponseEntity.ok(creatorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(creatorService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CreatorResponseDTO> create(@Valid @RequestBody CreatorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatorService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreatorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CreatorRequestDTO dto) {
        return ResponseEntity.ok(creatorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creatorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}