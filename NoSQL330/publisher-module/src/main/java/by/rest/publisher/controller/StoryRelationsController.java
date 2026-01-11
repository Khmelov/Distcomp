package by.rest.publisher.controller;

import by.rest.publisher.dto.EditorResponseTo;
import by.rest.publisher.dto.TagResponseTo;
import by.rest.publisher.dto.StoryResponseTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.exception.ApiException;
import by.rest.publisher.service.EditorService;
import by.rest.publisher.service.TagService;
import by.rest.publisher.service.StoryService;
import by.rest.publisher.client.CommentClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/relations")
public class StoryRelationsController {
    private final StoryService storyService;
    private final EditorService editorService;
    private final TagService tagService;
    private final CommentClient commentClient;

    public StoryRelationsController(StoryService storyService, EditorService editorService,
                                   TagService tagService, CommentClient commentClient) {
        this.storyService = storyService;
        this.editorService = editorService;
        this.tagService = tagService;
        this.commentClient = commentClient;
    }

    @GetMapping("/stories/{id}/editor")
    public EditorResponseTo getEditorByStory(@PathVariable("id") Long id) {
        StoryResponseTo story = storyService.getById(id);
        if (story.getEditorId() == null) {
            throw new ApiException(400, "40002", "Story has no editor");
        }
        return editorService.getById(story.getEditorId());
    }

    @GetMapping("/stories/{id}/comments")
    public List<CommentResponseTo> getCommentsByStory(
            @PathVariable("id") Long id) {
        return commentClient.getCommentsByStoryId(id);
    }

    @GetMapping("/stories")
    public Page<StoryResponseTo> getStories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {
        return storyService.getAll(page, size, sort);
    }

    @GetMapping("/stories/{id}/tags")
    public List<TagResponseTo> getTagsByStory(@PathVariable("id") Long id) {
        StoryResponseTo story = storyService.getById(id);
        if (story.getTagIds() == null || story.getTagIds().isEmpty()) {
            return List.of();
        }
        return story.getTagIds().stream()
                .map(tagService::getById)
                .toList();
    }
}