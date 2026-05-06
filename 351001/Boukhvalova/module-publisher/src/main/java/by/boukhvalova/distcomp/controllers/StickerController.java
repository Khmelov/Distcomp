package by.boukhvalova.distcomp.controllers;

import by.boukhvalova.distcomp.dto.StickerRequestTo;
import by.boukhvalova.distcomp.dto.StickerResponseTo;
import by.boukhvalova.distcomp.services.StickerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping({"/api/v1.0/stickers", "/api/v1.0/markers", "/api/v1.0/labels"})
@AllArgsConstructor
public class StickerController {
    private final StickerService serviceImpl;

    @GetMapping
    public Collection<StickerResponseTo> getAll(){
        return serviceImpl.getAll();
    }

    @GetMapping("/{id}")
    public StickerResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StickerResponseTo create(@RequestBody @Valid StickerRequestTo request){
        return serviceImpl.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public StickerResponseTo update(@RequestBody @Valid StickerRequestTo request){
        try{
            return serviceImpl.update(request);
        } catch( NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }
}
