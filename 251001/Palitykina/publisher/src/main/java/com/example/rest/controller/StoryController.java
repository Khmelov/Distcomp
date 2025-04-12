package com.example.rest.controller;

import com.example.rest.dto.requestDto.StoryRequestTo;
import com.example.rest.dto.responseDto.StoryResponseTo;
import com.example.rest.dto.updateDto.StoryUpdateTo;
import com.example.rest.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1.0/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping
    public Collection<StoryResponseTo> getAll() { return storyService.getAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@RequestBody @Valid StoryRequestTo input) { return storyService.create(input); }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public StoryResponseTo update(@RequestBody @Valid StoryUpdateTo input) {
        try{ return storyService.update(input); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public StoryResponseTo get(@PathVariable long id) {
        try{ return storyService.get(id); }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        boolean deleted = storyService.delete(id);
        if (!deleted) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
