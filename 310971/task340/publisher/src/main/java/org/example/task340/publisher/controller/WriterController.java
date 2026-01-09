package org.example.task340.publisher.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.task340.publisher.dto.WriterRequestTo;
import org.example.task340.publisher.dto.WriterResponseTo;
import org.example.task340.publisher.service.WriterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterController {

    private final WriterService service;

    public WriterController(WriterService service) {
        this.service = service;
    }

    @GetMapping
    public List<WriterResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WriterResponseTo getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(@Valid @RequestBody WriterRequestTo request) {
        WriterResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public WriterResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody WriterRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

