package com.homel.user_stories.controller;

import com.homel.user_stories.dto.StoryRequestTo;
import com.homel.user_stories.dto.StoryResponseTo;
import com.homel.user_stories.dto.UserRequestTo;
import com.homel.user_stories.dto.UserResponseTo;
import com.homel.user_stories.service.StoryService;
import com.homel.user_stories.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/storys")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo createStory(@Valid @RequestBody StoryRequestTo storyRequest) {
        return storyService.createStory(storyRequest);
    }

    @GetMapping("/{id}")
    public StoryResponseTo getStory(@PathVariable Long id) {
        return storyService.getStory(id);
    }

    @GetMapping
    public List<StoryResponseTo> getAllStories() {
        return storyService.getAllStories();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
    }

    @PutMapping
    public StoryResponseTo updateStory(@Valid @RequestBody StoryRequestTo storyRequest) {
        return storyService.updateStory(storyRequest);
    }
}
