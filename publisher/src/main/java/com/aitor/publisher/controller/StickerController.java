package com.aitor.publisher.controller;

import com.aitor.publisher.dto.StickerRequestTo;
import com.aitor.publisher.dto.StickerResponseTo;
import com.aitor.publisher.service.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("stickers")
class StickerController {
    private final StickerService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public StickerResponseTo add(@RequestBody @Valid StickerRequestTo request){
        return service.add(request);
    }

    @PutMapping("/{id}")
    public StickerResponseTo set(@PathVariable Long id, @RequestBody @Valid StickerRequestTo request){
        return service.set(id, request);
    }

    @GetMapping("/{id}")
    public StickerResponseTo get(@PathVariable Long id){
        return service.get(id);
    }

    @GetMapping()
    public List<StickerResponseTo> getAll(){
        return service.getAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public StickerResponseTo remove(@PathVariable Long id){
        return service.remove(id);
    }
}
