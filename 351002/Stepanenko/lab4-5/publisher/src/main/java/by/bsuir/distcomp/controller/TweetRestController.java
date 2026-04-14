package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.core.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetRestController {
    private final TweetService tweetService;

    public TweetRestController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll(
            @RequestParam(required = false) List<String> markerNames,
            @RequestParam(required = false) List<Long> markerIds,
            @RequestParam(required = false) String authorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] parts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        Pageable pageable = PageRequest.of(page, size, s);

        return ResponseEntity.ok(tweetService.search(markerNames, markerIds, authorLogin, title, content, pageable));
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.getById(id));
    }

    @PutMapping
    public ResponseEntity<TweetResponseTo> update(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.ok(tweetService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}