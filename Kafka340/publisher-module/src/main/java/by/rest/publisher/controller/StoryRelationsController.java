package by.rest.publisher.controller;

import by.rest.publisher.client.CommentClient;
import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.dto.TagResponseTo;
import by.rest.publisher.dto.StoryResponseTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.service.EditorService;
import by.rest.publisher.service.TagService;
import by.rest.publisher.service.StoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/relations")
public class StoryRelationsController {
    
    private final StoryService storyService;
    private final EditorService editorService;
    private final TagService tagService;
    private final CommentClient commentClient;
    
    public StoryRelationsController(StoryService storyService, 
                                   EditorService editorService,
                                   TagService tagService,
                                   CommentClient commentClient) {
        this.storyService = storyService;
        this.editorService = editorService;
        this.tagService = tagService;
        this.commentClient = commentClient;
    }
    
    @GetMapping("/stories/{id}/editor")
    public EditorResponseTo getEditorByStory(@PathVariable Long id) {
        StoryResponseTo story = storyService.getById(id);
        return editorService.getById(story.getEditorId());
    }
    
    @GetMapping("/stories/{id}/comments")
    public List<CommentResponseTo> getCommentsByStory(@PathVariable Long id) {
        try {
            return commentClient.getCommentsByStoryId(id);
        } catch (Exception e) {
            throw new ApiException(500, "50001", "Failed to get comments from discussion module: " + e.getMessage());
        }
    }
    
    @GetMapping("/stories/{id}/tags")
    public List<TagResponseTo> getTagsByStory(@PathVariable Long id) {
        StoryResponseTo story = storyService.getById(id);
        if (story.getTagIds() == null || story.getTagIds().isEmpty()) {
            return List.of();
        }
        return story.getTagIds().stream()
                .map(tagService::getById)
                .toList();
    }
    
    @GetMapping("/editors/{id}/stories")
    public List<StoryResponseTo> getStoriesByEditor(@PathVariable Long id) {
        return storyService.getStoriesByEditorId(id);
    }
    
    @GetMapping("/tags/{id}/stories")
    public List<StoryResponseTo> getStoriesByTag(@PathVariable Long id) {
        return storyService.getStoriesByTagId(id);
    }
    
    @GetMapping("/health")
    public String health() {
        return "Story Relations Controller is working!";
    }
}