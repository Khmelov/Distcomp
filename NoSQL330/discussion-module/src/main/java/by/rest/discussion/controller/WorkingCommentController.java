package by.rest.discussion.controller;

import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.service.SimpleCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments-working")
public class WorkingCommentController {
    
    private final SimpleCommentService service;
    
    public WorkingCommentController(SimpleCommentService service) {
        this.service = service;
    }
    
    @GetMapping("/test")
    public String test() {
        return "Working comment controller!";
    }
    
    @GetMapping
    public List<CommentResponseTo> getAll() {
        return service.getAll();
    }
    
    @PostMapping
    public CommentResponseTo create(@RequestBody CommentRequestTo request) {
        return service.create(request);
    }
}