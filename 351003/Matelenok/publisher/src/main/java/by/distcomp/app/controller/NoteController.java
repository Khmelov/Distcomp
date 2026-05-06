package by.distcomp.app.controller;

import by.distcomp.app.dto.NoteRequestTo;
import by.distcomp.app.dto.NoteResponseTo;
import by.distcomp.app.dto.NoteState;
import by.distcomp.app.kafka.NoteKafkaProducer;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {

    private final RestClient discussionClient;

    private final NoteKafkaProducer kafkaProducer;

    public NoteController(RestClient discussionClient, NoteKafkaProducer kafkaProducer) {
        this.discussionClient = discussionClient;
        this.kafkaProducer = kafkaProducer;
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
        long generatedId = request.id() != null ? request.id() : new Random().nextLong(10000);

        NoteRequestTo noteWithPending = new NoteRequestTo(
                generatedId,
                request.articleId(),
                request.content(),
                NoteState.PENDING
        );

        kafkaProducer.sendToModeration(noteWithPending);

        return new NoteResponseTo(
                noteWithPending.id(),
                noteWithPending.articleId(),
                noteWithPending.content(),
                NoteState.PENDING
        );
    }

    @PutMapping("/{id}")
    public NoteResponseTo updateNote(@PathVariable Long id, @Valid @RequestBody NoteRequestTo request) {
        return discussionClient.put()
                .uri("/{id}", id)
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