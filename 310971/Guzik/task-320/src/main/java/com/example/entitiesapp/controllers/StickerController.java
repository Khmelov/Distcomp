package com.example.entitiesapp.controllers;

import com.example.entitiesapp.dto.request.StickerRequestTo;
import com.example.entitiesapp.dto.response.StickerResponseTo;
import com.example.entitiesapp.services.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
@RequiredArgsConstructor
public class StickerController {
    private final StickerService stickerService;

    @GetMapping
    public ResponseEntity<List<StickerResponseTo>> getAllStickers() {
        List<StickerResponseTo> stickers = stickerService.getAll();
        return ResponseEntity.ok(stickers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StickerResponseTo> getStickerById(@PathVariable Long id) {
        StickerResponseTo sticker = stickerService.getById(id);
        return ResponseEntity.ok(sticker);
    }

    @PostMapping
    public ResponseEntity<StickerResponseTo> createSticker(@Valid @RequestBody StickerRequestTo stickerRequestTo) {
        StickerResponseTo createdSticker = stickerService.create(stickerRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSticker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StickerResponseTo> updateSticker(
            @PathVariable Long id,
            @Valid @RequestBody StickerRequestTo stickerRequestTo) {
        StickerResponseTo updatedSticker = stickerService.update(id, stickerRequestTo);
        return ResponseEntity.ok(updatedSticker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSticker(@PathVariable Long id) {
        stickerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}