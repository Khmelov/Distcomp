package by.distcomp.app.controller;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {

    private final RestClient discussionClient;

    public NoteController(RestClient discussionClient) {
        this.discussionClient = discussionClient;
    }

    @GetMapping
    public List<NoteResponseTo> getAllNotes() {
        return discussionClient.get()
                .uri("")
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteResponseTo>>() {});
    }

    @GetMapping("/article/{articleId}")
    public List<NoteResponseTo> getNotesByArticle(@PathVariable Long articleId) {
        return discussionClient.get()
                .uri("/article/{articleId}", articleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteResponseTo>>() {});
    }

    @GetMapping("/{id}")
    public NoteResponseTo getNote(@PathVariable Long id) {
        return discussionClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo createNote(@Valid @RequestBody NoteRequestTo request) {
        return discussionClient.post()
                .uri("")
                .body(request)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    @PutMapping
    public NoteResponseTo updateNote(@Valid @RequestBody NoteRequestTo request) {
        return discussionClient.put()
                .uri("")
                .body(request)
                .retrieve()
                .body(NoteResponseTo.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        discussionClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return ResponseEntity.noContent().build();
    }
}