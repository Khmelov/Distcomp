package com.example.task310.controller;

import com.example.task310.dto.request.StickerRequestTo;
import com.example.task310.dto.response.StickerResponseTo;
import com.example.task310.service.StickerService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerController {

    private final StickerService service;

    public StickerController(StickerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StickerResponseTo> create(@Valid @RequestBody StickerRequestTo body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    @GetMapping
    public List<StickerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public StickerResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public StickerResponseTo update(@PathVariable long id, @Valid @RequestBody StickerRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
