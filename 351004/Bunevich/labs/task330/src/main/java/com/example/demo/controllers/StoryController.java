package com.example.demo.controllers;

import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.servises.PostService;
import com.example.demo.servises.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1.0/stories")
@RestController
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StoryResponseTo> getAllStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title){
        return storyService.findAll(page, size, sortBy, sortDir, title);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo createStory(@Valid @RequestBody StoryRequestTo storyRequestTo){
        return storyService.create(storyRequestTo);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long id){
        storyService.delete(id);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StoryResponseTo findStoryById(@PathVariable Long id){
        return storyService.findById(id);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StoryResponseTo updateStory(@PathVariable Long id, @Valid @RequestBody StoryRequestTo storyRequestTo){
        return storyService.update(id, storyRequestTo);
    }
}
