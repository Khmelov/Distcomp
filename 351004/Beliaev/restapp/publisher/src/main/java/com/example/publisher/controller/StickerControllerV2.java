package com.example.publisher.controller;

import com.example.publisher.dto.request.StickerRequestTo;
import com.example.publisher.dto.response.StickerResponseTo;
import com.example.publisher.service.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/stickers")
@RequiredArgsConstructor
public class StickerControllerV2 {
    private final StickerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StickerResponseTo create(@Valid @RequestBody StickerRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<StickerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public StickerResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StickerResponseTo update(@PathVariable Long id, @Valid @RequestBody StickerRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}