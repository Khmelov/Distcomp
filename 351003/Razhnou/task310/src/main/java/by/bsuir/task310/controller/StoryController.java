package by.bsuir.task310.controller;

import by.bsuir.task310.dto.request.StoryRequestTo;
import by.bsuir.task310.dto.response.StoryResponseTo;
import by.bsuir.task310.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<StoryResponseTo> create(@RequestBody StoryRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storyService.create(request));
    }

    @GetMapping
    public List<StoryResponseTo> findAll() {
        return storyService.findAll();
    }

    @GetMapping("/{id}")
    public StoryResponseTo findById(@PathVariable Long id) {
        return storyService.findById(id);
    }

    @PutMapping
    public StoryResponseTo update(@RequestBody StoryRequestTo request) {
        return storyService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
