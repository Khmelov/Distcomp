package by.rest.discussion.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments-simple")
public class SimpleCommentController {
    
    @GetMapping("/test")
    public String test() {
        return "Simple comment controller works!";
    }
    
    @GetMapping
    public List<String> getAll() {
        return List.of("Comment 1", "Comment 2", "Comment 3");
    }
    
    @PostMapping
    public String create(@RequestBody String content) {
        return "Created comment: " + content;
    }
}