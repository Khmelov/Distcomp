package com.lab1.lab1DC.controller;

import com.lab1.lab1DC.dto.*;
import com.lab1.lab1DC.entity.Sticker;
import com.lab1.lab1DC.service.StickerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerController {

    private final StickerService stickerService;

    public StickerController(StickerService stickerService) {
        this.stickerService = stickerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StickerResponseTo> createSticker(@RequestBody StickerRequestTo stickerRequestTo) {
        try {
            if(!validateSticker(stickerRequestTo)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StickerResponseTo());
            }
            Long id = System.currentTimeMillis();
            Sticker sticker = new Sticker(id, stickerRequestTo.getName());
            StickerResponseTo response = stickerService.createSticker(sticker);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StickerResponseTo());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StickerResponseTo());
        }
    }

    @GetMapping
    public ResponseEntity<List<StickerResponseTo>> getAllStickers() {
        try {
            List<StickerResponseTo> response = stickerService.getAllStickers();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StickerResponseTo> getStickerById(@PathVariable Long id) {
        try {
            StickerResponseTo response = stickerService.getStickerById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StickerResponseTo());
        }
    }

    @PutMapping
    public ResponseEntity<StickerResponseTo> updateNews(@RequestBody StickerRequestTo stickerRequestTo) {
        try {
            if (stickerRequestTo.getId() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StickerResponseTo());

            StickerResponseTo updatedSticker = stickerService.updateSticker(stickerRequestTo.getId(), stickerRequestTo);
            return ResponseEntity.ok(updatedSticker);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StickerResponseTo());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StickerResponseTo());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StickerResponseTo());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSticker(@PathVariable Long id) {
        try {
            stickerService.deleteSticker(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private boolean validateSticker(StickerRequestTo stickerRequestTo) {

        if (stickerRequestTo.getName().length() < 2 ||
                stickerRequestTo.getName().length() > 32) {
            return false;
        }

        return true;
    }
}

