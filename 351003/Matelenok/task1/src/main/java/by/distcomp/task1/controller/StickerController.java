package by.distcomp.task1.controller;

import by.distcomp.task1.dto.StickerRequestTo;
import by.distcomp.task1.dto.StickerResponseTo;
import by.distcomp.task1.service.StickerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerController {
    private final StickerService stickerService;

    public StickerController(StickerService stickerService) {
        this.stickerService = stickerService;
    }
    @GetMapping
    public List<StickerResponseTo> getAllStickers(){
        return stickerService.getAllStickers();
    }
    @GetMapping("/{sticker-id}")
    public StickerResponseTo getSticker(@PathVariable ("sticker-id") Long stickerId){
        return stickerService.getStickerById(stickerId);
    }
    @PostMapping
    public ResponseEntity<StickerResponseTo> createSticker(@Valid @RequestBody StickerRequestTo request){
        StickerResponseTo createdSticker = stickerService.createSticker(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSticker.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdSticker);
    }
    @PutMapping("/{sticker-id}")
    public StickerResponseTo updateSticker(@PathVariable ("sticker-id") Long stickerId, @Valid @RequestBody StickerRequestTo request){
        return stickerService.updateSticker(stickerId,request);
    }
    @DeleteMapping("/{sticker-id}")
    public ResponseEntity<Void> deleteSticker(@PathVariable ("sticker-id") Long stickerId){
        stickerService.deleteSticker(stickerId);
        return ResponseEntity.noContent().build();
    }
}