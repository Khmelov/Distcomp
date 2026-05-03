package com.example.demo.controllers;

import com.example.demo.dto.request.TagRequestTo;
import com.example.demo.dto.response.TagResponseTo;
import com.example.demo.servises.StoryService;
import com.example.demo.servises.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {
    public final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagResponseTo> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name){
        return tagService.getTag(page, size, sortBy, sortDir, name);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseTo create(@Valid @RequestBody TagRequestTo request){
        return tagService.create(request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        tagService.deleteTag(id);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponseTo getTagById(@PathVariable Long id){
        return tagService.findTagById(id);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponseTo update(@PathVariable Long id, @Valid @RequestBody TagRequestTo request){
        return tagService.uptadeTag(id, request);
    }
}
