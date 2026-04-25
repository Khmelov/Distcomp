package by.tracker.rest_api.controller;

import by.tracker.rest_api.entity.Note;
import by.tracker.rest_api.entity.Tweet;
import by.tracker.rest_api.repository.NoteRepository;
import by.tracker.rest_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return noteRepository.findAll().stream()
                .map(this::toResponseMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(toResponseMap(note)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            Long tweetId = Long.valueOf(payload.get("tweetId").toString());
            String content = payload.get("content").toString();

            if (content.length() < 2 || content.length() > 2048) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Content must be between 2 and 2048 characters");
                error.put("errorCode", 40018);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(error);
            }

            Tweet tweet = tweetRepository.findById(tweetId).orElse(null);
            if (tweet == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Tweet not found");
                error.put("errorCode", 40402);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(error);
            }

            Note note = new Note();
            note.setContent(content);
            note.setTweet(tweet);

            Note saved = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponseMap(saved));

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            error.put("errorCode", 40000);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Note existingNote = noteRepository.findById(id).orElse(null);
            if (existingNote == null) {
                return ResponseEntity.notFound().build();
            }

            if (payload.containsKey("content")) {
                String content = payload.get("content").toString();
                if (content.length() < 2 || content.length() > 2048) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Content must be between 2 and 2048 characters");
                    error.put("errorCode", 40018);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.badRequest().body(error);
                }
                existingNote.setContent(content);
            }

            if (payload.containsKey("tweetId")) {
                Long tweetId = Long.valueOf(payload.get("tweetId").toString());
                Tweet tweet = tweetRepository.findById(tweetId).orElse(null);
                if (tweet == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorMessage", "Tweet not found");
                    error.put("errorCode", 40402);
                    error.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.badRequest().body(error);
                }
                existingNote.setTweet(tweet);
            }

            Note saved = noteRepository.save(existingNote);
            return ResponseEntity.ok(toResponseMap(saved));

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            error.put("errorCode", 40000);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!noteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        noteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toResponseMap(Note note) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", note.getId());
        response.put("tweetId", note.getTweet() != null ? note.getTweet().getId() : null);
        response.put("content", note.getContent());
        return response;
    }
}