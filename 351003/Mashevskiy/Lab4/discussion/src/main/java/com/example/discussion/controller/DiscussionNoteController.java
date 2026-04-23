package com.example.discussion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/notes")
public class DiscussionNoteController {

    private static final Map<Long, NoteInfo> notes = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public NoteInfo getNote(@PathVariable Long id) {
        System.out.println("GET /notes/" + id + " - Current storage: " + notes.keySet());

        NoteInfo note = notes.get(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        return note;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteInfo createNote(@RequestBody NoteInfo note) {
        notes.put(note.getId(), note);
        System.out.println("Created note " + note.getId() + " - Storage: " + notes.keySet());
        return note;
    }

    @PutMapping("/{id}")
    public NoteInfo updateNote(@PathVariable Long id, @RequestBody NoteInfo note) {
        note.setId(id);
        notes.put(id, note);
        return note;
    }
    @GetMapping("/all")
    public Map<Long, NoteInfo> getAllNotes() {
        return notes;
    }
    public void deleteNote(Long id) {
        NoteInfo removed = notes.remove(id);
        if (removed != null) {
            System.out.println("SUCCESS: Note " + id + " deleted from discussion storage");
        } else {
            System.out.println("WARNING: Note " + id + " not found in storage");
        }
        System.out.println("Current storage: " + notes.keySet());
    }

    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllNotes() {
        notes.clear();
        System.out.println("All notes cleared");
    }

    public void saveNote(NoteInfo note) {
        notes.put(note.getId(), note);
        System.out.println("SAVED via Kafka: Note " + note.getId() + " - Storage: " + notes.keySet());
    }

    public static class NoteInfo {
        private Long id;
        private Long tweetId;
        private String content;
        private String state;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getTweetId() { return tweetId; }
        public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}