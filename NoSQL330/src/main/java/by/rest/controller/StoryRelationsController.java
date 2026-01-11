package by.rest.controller;

import by.rest.dto.EditorResponseTo;
import by.rest.dto.CommentResponseTo;
import by.rest.dto.TagResponseTo;
import by.rest.dto.StoryResponseTo;
import by.rest.exception.ApiException;
import by.rest.service.CommentService;
import by.rest.service.EditorService;
import by.rest.service.StoryService;
import by.rest.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/relations")
public class StoryRelationsController {
    private final StoryService storyService;
    private final EditorService editorService;
    private final TagService tagService;
    private final CommentService commentService;

    public StoryRelationsController(StoryService storyService, EditorService editorService,
                                   TagService tagService, CommentService commentService) {
        this.storyService = storyService;
        this.editorService = editorService;
        this.tagService = tagService;
        this.commentService = commentService;
    }

    @GetMapping("/stories/{id}/editor")
    public EditorResponseTo getEditorByStory(@PathVariable Long id) {
        StoryResponseTo story = storyService.getById(id);
        if (story.getEditorId() == null) {
            throw new ApiException(400, "40002", "Story has no editor");
        }
        return editorService.getById(story.getEditorId());
    }

    @GetMapping("/comments")
    public Page<CommentResponseTo> getComments(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return commentService.getAll(page, size);
    }

    @GetMapping("/stories")
    public Page<StoryResponseTo> getStories(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(defaultValue = "id,asc") String sort) {
        return storyService.getAll(page, size, sort);
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
}