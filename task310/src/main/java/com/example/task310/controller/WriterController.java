package com.example.task310.controller;

import com.example.task310.dto.request.WriterRequestTo;
import com.example.task310.dto.response.WriterResponseTo;
import com.example.task310.service.WriterService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterController {

    private final WriterService service;

    public WriterController(WriterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(@Valid @RequestBody WriterRequestTo body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    @GetMapping
    public List<WriterResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WriterResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public WriterResponseTo update(@PathVariable long id, @Valid @RequestBody WriterRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
