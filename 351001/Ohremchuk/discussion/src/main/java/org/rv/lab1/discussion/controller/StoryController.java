package org.rv.lab1.discussion.controller;

import org.rv.lab1.discussion.api.ApiPaths;
import org.rv.lab1.discussion.dto.CommentResponseTo;
import org.rv.lab1.discussion.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/stories")
public class StoryController {
    private final CommentService commentService;

    public StoryController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponseTo> getCommentsByStoryId(@PathVariable long id) {
        return commentService.getByStoryId(id);
    }
}

