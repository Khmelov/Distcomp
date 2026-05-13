package by.bsuir.task310.web;

import by.bsuir.task310.dto.request.StickerRequestTo;
import by.bsuir.task310.dto.response.StickerResponseTo;
import by.bsuir.task310.service.StickerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StickerResponseTo> create(@Valid @RequestBody StickerRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StickerResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StickerResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping
    public ResponseEntity<StickerResponseTo> update(@Valid @RequestBody StickerRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}