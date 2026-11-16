package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.dto.response.StoryResponseTo;
import by.bsuir.rest.dto.response.UserResponseTo;
import by.bsuir.rest.service.MessageService;
import by.bsuir.rest.service.StorySearchCriteria;
import by.bsuir.rest.service.StoryService;
import by.bsuir.rest.web.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.STORIES)
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<StoryResponseTo> create(@RequestBody StoryRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseTo>> getAll(
            @RequestParam(required = false) List<String> markerName,
            @RequestParam(required = false) List<Long> markerId,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        StorySearchCriteria criteria = new StorySearchCriteria(markerName, markerId, login, title, content);
        return ResponseEntity.ok(storyService.find(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getById(id));
    }

    @GetMapping("/{id}/user")
    public ResponseEntity<UserResponseTo> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getAuthor(id));
    }

    @GetMapping("/{id}/markers")
    public ResponseEntity<List<MarkerResponseTo>> getMarkers(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getMarkers(id));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponseTo>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findByStoryId(id));
    }

    @PutMapping
    public ResponseEntity<StoryResponseTo> update(@RequestBody StoryRequestTo request) {
        return ResponseEntity.ok(storyService.update(null, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryResponseTo> update(@PathVariable Long id, @RequestBody StoryRequestTo request) {
        return ResponseEntity.ok(storyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
