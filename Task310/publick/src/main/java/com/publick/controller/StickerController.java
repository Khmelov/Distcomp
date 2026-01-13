package com.publick.controller;

import com.publick.dto.StickerRequestTo;
import com.publick.dto.StickerResponseTo;
import com.publick.service.StickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/stickers")
@Validated
public class StickerController {

    @Autowired
    private StickerService stickerService;

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
    public ResponseEntity<StickerResponseTo> createSticker(@Valid @RequestBody StickerRequestTo request) {
        StickerResponseTo created = stickerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StickerResponseTo> updateSticker(@PathVariable Long id, @Valid @RequestBody StickerRequestTo request) {
        StickerResponseTo updated = stickerService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSticker(@PathVariable Long id) {
        stickerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}