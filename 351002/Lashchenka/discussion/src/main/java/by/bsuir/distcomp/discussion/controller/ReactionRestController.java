package by.bsuir.distcomp.discussion.controller;

import by.bsuir.distcomp.discussion.api.DiscussionApiPaths;
import by.bsuir.distcomp.discussion.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.discussion.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.discussion.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = DiscussionApiPaths.REACTIONS,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ReactionRestController {

    private final ReactionService reactionService;

    public ReactionRestController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo dto) {
        ReactionResponseTo response = reactionService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getById(@PathVariable Long id) {
        ReactionResponseTo response = reactionService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        List<ReactionResponseTo> response = reactionService.getAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReactionResponseTo> update(@Valid @RequestBody ReactionRequestTo dto) {
        ReactionResponseTo response = reactionService.update(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        reactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
