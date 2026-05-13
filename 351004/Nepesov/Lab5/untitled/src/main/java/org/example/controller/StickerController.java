package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.StickerRequestTo;
import org.example.dto.StickerResponseTo;
import org.example.service.StickerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService service;

    @GetMapping
    public List<StickerResponseTo> findAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StickerResponseTo create(@Valid @RequestBody StickerRequestTo request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public StickerResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping
    public StickerResponseTo update(@Valid @RequestBody StickerRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.existsById(id)) { // Проверяем существование
            service.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Это то, что ждет тест!
        }
    }
}