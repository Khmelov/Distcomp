package by.distcomp.app.controller;

import by.distcomp.app.dto.StickerRequestTo;
import by.distcomp.app.dto.StickerResponseTo;
import by.distcomp.app.service.StickerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public List<StickerResponseTo> getStickers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return stickerService.getStickersPage(pageable);
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