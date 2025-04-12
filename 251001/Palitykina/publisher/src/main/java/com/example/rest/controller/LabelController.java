package com.example.rest.controller;

import com.example.rest.dto.requestDto.LabelRequestTo;
import com.example.rest.dto.responseDto.LabelResponseTo;
import com.example.rest.dto.updateDto.LabelUpdateTo;
import com.example.rest.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public Collection<LabelResponseTo> getAll() { return labelService.getAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo create(@RequestBody @Valid LabelRequestTo input) { return labelService.create(input); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseTo update(@RequestBody @Valid LabelUpdateTo input) {
        try{ return labelService.update(input); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public LabelResponseTo get(@PathVariable long id) {
        try{ return labelService.get(id); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        boolean deleted = labelService.delete(id);
        if (!deleted) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
