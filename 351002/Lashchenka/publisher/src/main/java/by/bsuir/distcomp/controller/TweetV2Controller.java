package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.exception.ForbiddenException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.security.EditorAuthPrincipal;
import by.bsuir.distcomp.security.V2Security;
import by.bsuir.distcomp.repository.TweetRepository;
import by.bsuir.distcomp.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tweets")
public class TweetV2Controller {

    private final TweetService tweetService;
    private final TweetRepository tweetRepository;

    public TweetV2Controller(TweetService tweetService, TweetRepository tweetRepository) {
        this.tweetService = tweetService;
        this.tweetRepository = tweetRepository;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo dto) {
        EditorAuthPrincipal p = V2Security.currentEditor();
        if (!V2Security.isAdmin(p) && !p.getEditorId().equals(dto.getEditorId())) {
            throw new ForbiddenException("Cannot create tweet for another editor", 40304);
        }
        TweetResponseTo response = tweetService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll() {
        return ResponseEntity.ok(tweetService.getAll());
    }

    @PutMapping
    public ResponseEntity<TweetResponseTo> update(@Valid @RequestBody TweetRequestTo dto) {
        EditorAuthPrincipal p = V2Security.currentEditor();
        Tweet existing = tweetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + dto.getId() + " not found", 40405));
        ensureTweetWriteAccess(p, existing, dto.getEditorId());
        return ResponseEntity.ok(tweetService.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        EditorAuthPrincipal p = V2Security.currentEditor();
        Tweet existing = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found", 40408));
        if (!V2Security.isAdmin(p)) {
            if (!existing.getEditorId().equals(p.getEditorId())) {
                throw new ForbiddenException("Not allowed to delete this tweet", 40306);
            }
        }
        tweetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static void ensureTweetWriteAccess(EditorAuthPrincipal p, Tweet existing, Long dtoEditorId) {
        if (V2Security.isAdmin(p)) {
            return;
        }
        if (!existing.getEditorId().equals(p.getEditorId())) {
            throw new ForbiddenException("Not allowed to modify this tweet", 40307);
        }
        if (!p.getEditorId().equals(dtoEditorId)) {
            throw new ForbiddenException("Cannot reassign tweet to another editor", 40303);
        }
    }
}
