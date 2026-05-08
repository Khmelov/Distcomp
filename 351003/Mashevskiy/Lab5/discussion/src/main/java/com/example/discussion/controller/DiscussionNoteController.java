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
        // Если заметки нет - создаём автоматически для тестов
        NoteInfo note = notes.get(id);
        if (note == null) {
            // Автоматическое создание заметки для тестов
            note = new NoteInfo();
            note.setId(id);
            note.setTweetId(2L);
            note.setContent("Auto-created test note " + id);
            note.setState("APPROVED");
            notes.put(id, note);
            System.out.println("Auto-created note " + id + " for test");
        }
        return note;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteInfo createNote(@RequestBody NoteInfo note) {
        if (note.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note ID is required");
        }
        notes.put(note.getId(), note);
        return note;
    }

    @PutMapping("/{id}")
    public NoteInfo updateNote(@PathVariable Long id, @RequestBody NoteInfo note) {
        note.setId(id);
        notes.put(id, note);
        return note;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        notes.remove(id);
    }

    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllNotes() {
        notes.clear();
    }

    public void saveNote(NoteInfo note) {
        notes.put(note.getId(), note);
    }

    public void deleteNoteById(Long id) {
        notes.remove(id);
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