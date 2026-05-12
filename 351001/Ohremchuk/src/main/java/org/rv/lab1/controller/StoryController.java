package org.rv.lab1.controller;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.CommentResponseTo;
import org.rv.lab1.dto.EditorResponseTo;
import org.rv.lab1.dto.MarkerResponseTo;
import org.rv.lab1.dto.StoryRequestTo;
import org.rv.lab1.dto.StoryResponseTo;
import org.rv.lab1.mapper.EditorMapper;
import org.rv.lab1.mapper.MarkerMapper;
import org.rv.lab1.service.CommentService;
import org.rv.lab1.service.EditorService;
import org.rv.lab1.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/stories")
public class StoryController {
    private final StoryService storyService;
    private final EditorService editorService;
    private final CommentService commentService;
    private final EditorMapper editorMapper;
    private final MarkerMapper markerMapper;

    public StoryController(
            StoryService storyService,
            EditorService editorService,
            CommentService commentService,
            EditorMapper editorMapper,
            MarkerMapper markerMapper
    ) {
        this.storyService = storyService;
        this.editorService = editorService;
        this.commentService = commentService;
        this.editorMapper = editorMapper;
        this.markerMapper = markerMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@Valid @RequestBody StoryRequestTo request) {
        return storyService.create(request);
    }

    @GetMapping
    public List<StoryResponseTo> getAll() {
        return storyService.getAll();
    }

    @GetMapping("/{id}")
    public StoryResponseTo getById(@PathVariable long id) {
        return storyService.getById(id);
    }

    @PutMapping("/{id}")
    public StoryResponseTo update(@PathVariable long id, @Valid @RequestBody StoryRequestTo request) {
        return storyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        storyService.delete(id);
    }

    @GetMapping("/{id}/editor")
    public EditorResponseTo getEditorByStoryId(@PathVariable long id) {
        var editor = storyService.findEntity(id).getEditor();
        return editorMapper.toResponse(editorService.findEntity(editor.getId()));
    }

    @GetMapping("/{id}/markers")
    public List<MarkerResponseTo> getMarkersByStoryId(@PathVariable long id) {
        var markers = storyService.findEntity(id).getMarkers();
        if (markers == null || markers.isEmpty()) {
            return List.of();
        }
        return markers.stream().map(markerMapper::toResponse).toList();
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponseTo> getCommentsByStoryId(@PathVariable long id) {
        return commentService.getByStoryId(id);
    }

    @GetMapping("/search")
    public List<StoryResponseTo> search(
            @RequestParam(required = false) Set<Long> markerIds,
            @RequestParam(required = false) Set<String> markerNames,
            @RequestParam(required = false) String editorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content
    ) {
        return storyService.search(markerIds, markerNames, editorLogin, title, content);
    }
}

