package com.example.publisher.controller;

import com.example.publisher.dto.StickerRequestTo;
import com.example.publisher.dto.StickerResponseTo;
import com.example.publisher.service.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
@RequiredArgsConstructor
public class StickerController {

    private final StickerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StickerResponseTo create(@Valid @RequestBody StickerRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<StickerResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public StickerResponseTo get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public StickerResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody StickerRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}