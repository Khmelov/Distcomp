package by.bsuir.task310.controller;

import by.bsuir.task310.dto.request.EditorRequestTo;
import by.bsuir.task310.dto.response.EditorResponseTo;
import by.bsuir.task310.service.EditorService;
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
@RequestMapping("/api/v1.0/users")
public class EditorController {
    private final EditorService editorService;

    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> create(@RequestBody EditorRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorService.create(request));
    }

    @GetMapping
    public List<EditorResponseTo> findAll() {
        return editorService.findAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo findById(@PathVariable Long id) {
        return editorService.findById(id);
    }

    @GetMapping("/byTweet/{tweetId}")
    public EditorResponseTo findByStoryId(@PathVariable Long tweetId) {
        return editorService.findByStoryId(tweetId);
    }

    @PutMapping
    public EditorResponseTo update(@RequestBody EditorRequestTo request) {
        return editorService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        editorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
