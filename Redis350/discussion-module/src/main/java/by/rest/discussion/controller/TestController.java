package by.rest.discussion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/api/test")
    public String test() {
        return "Discussion test endpoint is working!";
    }
    
    @GetMapping("/api/test/comments")
    public String testComments() {
        return "Comments endpoint would be here";
    }
}