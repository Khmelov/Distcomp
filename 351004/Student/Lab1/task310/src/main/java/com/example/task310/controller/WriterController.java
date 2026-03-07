package com.example.task310.controller;

import com.example.task310.dto.WriterRequestTo;
import com.example.task310.dto.WriterResponseTo;
import com.example.task310.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {
    private final WriterService service;

    @GetMapping
    public List<WriterResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WriterResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo create(@Valid @RequestBody WriterRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping
    public WriterResponseTo update(@Valid @RequestBody WriterRequestTo dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}