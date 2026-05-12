package by.bsuir.task330.publisher.controller;

import jakarta.validation.Valid;
import by.bsuir.task330.publisher.service.StickerService;
import by.bsuir.task330.publisher.dto.request.StickerRequestTo;
import by.bsuir.task330.publisher.dto.response.StickerResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerController {

    private final StickerService stickerService;

    public StickerController(StickerService stickerService) {
        this.stickerService = stickerService;
    }

    @GetMapping("/{id}")
    public StickerResponseTo getById(@PathVariable Long id) {
        return stickerService.findById(id);
    }

    @GetMapping
    public List<StickerResponseTo> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter
    ) {
        return stickerService.findAll(page, size, sort, filter);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StickerResponseTo create(@Valid @RequestBody StickerRequestTo request) {
        return stickerService.create(request);
    }

    @PutMapping
    public StickerResponseTo update(@Valid @RequestBody StickerRequestTo request) {
        return stickerService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        stickerService.delete(id);
    }
}