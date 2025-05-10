package com.example.discussion.controller;

import com.example.discussion.dto.StickerRequestTo;
import com.example.discussion.dto.StickerResponseTo;
import com.example.discussion.service.StickerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerController {

    private final StickerService stickerService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public StickerController(StickerService stickerService, RedisTemplate<String, Object> redisTemplate) {
        this.stickerService = stickerService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getAllStickers() {
        try {
            String cacheKey = "allStickers";
            List<StickerResponseTo> stickers = (List<StickerResponseTo>) redisTemplate.opsForValue().get(cacheKey);

            if (stickers == null) {
                stickers = stickerService.findAll();
                redisTemplate.opsForValue().set(cacheKey, stickers, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(stickers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving stickers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStickerById(@PathVariable Long id) {
        try {
            String cacheKey = "sticker:" + id;
            StickerResponseTo sticker = (StickerResponseTo) redisTemplate.opsForValue().get(cacheKey);

            if (sticker == null) {
                sticker = stickerService.findById(id);
                if (sticker != null) {
                    redisTemplate.opsForValue().set(cacheKey, sticker, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("Sticker not found", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(sticker, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving sticker", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createSticker(@Valid @RequestBody StickerRequestTo stickerRequestTo) {
        try {
            StickerResponseTo createdSticker = stickerService.save(stickerRequestTo);
            redisTemplate.delete("allStickers");
            return new ResponseEntity<>(createdSticker, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating sticker", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateSticker(@Valid @RequestBody StickerRequestTo stickerRequestTo) {
        try {
            StickerResponseTo updatedSticker = stickerService.update(stickerRequestTo);
            if (updatedSticker != null) {
                redisTemplate.delete("allStickers");
                redisTemplate.delete("sticker:" + updatedSticker.getId());
                return new ResponseEntity<>(updatedSticker, HttpStatus.OK);
            }
            return new ResponseEntity<>("Sticker not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating sticker", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSticker(@PathVariable Long id) {
        stickerService.deleteById(id);
        redisTemplate.delete("allStickers");
        redisTemplate.delete("sticker:" + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}