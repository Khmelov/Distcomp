package com.example.kafkademo.controller.v1;

import com.example.kafkademo.entity.Note;
import com.example.kafkademo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notes")
public class NoteControllerV1 {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> getAll() {
        return noteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id) {
        return noteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-tweet/{tweetId}")
    public List<Note> getByTweetId(@PathVariable Long tweetId) {
        return noteService.findByTweetId(tweetId);
    }

    @PostMapping
    public ResponseEntity<Note> create(@RequestBody Note note) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.save(note));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @RequestBody Note note) {
        if (!noteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        note.setId(id);
        return ResponseEntity.ok(noteService.save(note));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!noteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        noteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}