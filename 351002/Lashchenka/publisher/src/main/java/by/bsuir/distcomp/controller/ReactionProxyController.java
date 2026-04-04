package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.client.DiscussionReactionsClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionProxyController {

    private final DiscussionReactionsClient discussionReactionsClient;

    public ReactionProxyController(DiscussionReactionsClient discussionReactionsClient) {
        this.discussionReactionsClient = discussionReactionsClient;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody String body) {
        return discussionReactionsClient.create(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        return discussionReactionsClient.getById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAll() {
        return discussionReactionsClient.getAll();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@RequestBody String body) {
        return discussionReactionsClient.update(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return discussionReactionsClient.deleteById(id);
    }
}
